/**
 * RedirectUrlResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService;

public class RedirectUrlResponse  implements java.io.Serializable {
    private java.lang.String redirectUrl;

    private java.lang.String wcTxnId;

    private java.lang.String merchantTnxId;

    private java.lang.String transactionStatus;

    private java.lang.String errorCode;

    public RedirectUrlResponse() {
    }

    public RedirectUrlResponse(
           java.lang.String redirectUrl,
           java.lang.String wcTxnId,
           java.lang.String merchantTnxId,
           java.lang.String transactionStatus,
           java.lang.String errorCode) {
           this.redirectUrl = redirectUrl;
           this.wcTxnId = wcTxnId;
           this.merchantTnxId = merchantTnxId;
           this.transactionStatus = transactionStatus;
           this.errorCode = errorCode;
    }


    /**
     * Gets the redirectUrl value for this RedirectUrlResponse.
     * 
     * @return redirectUrl
     */
    public java.lang.String getRedirectUrl() {
        return redirectUrl;
    }


    /**
     * Sets the redirectUrl value for this RedirectUrlResponse.
     * 
     * @param redirectUrl
     */
    public void setRedirectUrl(java.lang.String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    /**
     * Gets the wcTxnId value for this RedirectUrlResponse.
     * 
     * @return wcTxnId
     */
    public java.lang.String getWcTxnId() {
        return wcTxnId;
    }


    /**
     * Sets the wcTxnId value for this RedirectUrlResponse.
     * 
     * @param wcTxnId
     */
    public void setWcTxnId(java.lang.String wcTxnId) {
        this.wcTxnId = wcTxnId;
    }


    /**
     * Gets the merchantTnxId value for this RedirectUrlResponse.
     * 
     * @return merchantTnxId
     */
    public java.lang.String getMerchantTnxId() {
        return merchantTnxId;
    }


    /**
     * Sets the merchantTnxId value for this RedirectUrlResponse.
     * 
     * @param merchantTnxId
     */
    public void setMerchantTnxId(java.lang.String merchantTnxId) {
        this.merchantTnxId = merchantTnxId;
    }


    /**
     * Gets the transactionStatus value for this RedirectUrlResponse.
     * 
     * @return transactionStatus
     */
    public java.lang.String getTransactionStatus() {
        return transactionStatus;
    }


    /**
     * Sets the transactionStatus value for this RedirectUrlResponse.
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(java.lang.String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }


    /**
     * Gets the errorCode value for this RedirectUrlResponse.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this RedirectUrlResponse.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RedirectUrlResponse)) return false;
        RedirectUrlResponse other = (RedirectUrlResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.redirectUrl==null && other.getRedirectUrl()==null) || 
             (this.redirectUrl!=null &&
              this.redirectUrl.equals(other.getRedirectUrl()))) &&
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
              this.errorCode.equals(other.getErrorCode())));
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
        if (getRedirectUrl() != null) {
            _hashCode += getRedirectUrl().hashCode();
        }
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RedirectUrlResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", ">RedirectUrlResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("redirectUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "redirectUrl"));
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

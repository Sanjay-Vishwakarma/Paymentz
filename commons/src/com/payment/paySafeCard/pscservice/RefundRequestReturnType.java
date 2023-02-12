/**
 * RefundRequestReturnType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class RefundRequestReturnType  implements java.io.Serializable {
    private java.lang.String rtid;

    private java.lang.String mtid;

    private java.lang.String requestedCurrency;

    private double requestedAmount;

    private java.lang.String refundedInCurrency;

    private java.lang.Double refundedAmount;

    private java.lang.Boolean validationOnly;

    private int resultCode;

    private int errorCode;

    private java.lang.String errorCodeDescription;

    public RefundRequestReturnType() {
    }

    public RefundRequestReturnType(
           java.lang.String rtid,
           java.lang.String mtid,
           java.lang.String requestedCurrency,
           double requestedAmount,
           java.lang.String refundedInCurrency,
           java.lang.Double refundedAmount,
           java.lang.Boolean validationOnly,
           int resultCode,
           int errorCode,
           java.lang.String errorCodeDescription) {
           this.rtid = rtid;
           this.mtid = mtid;
           this.requestedCurrency = requestedCurrency;
           this.requestedAmount = requestedAmount;
           this.refundedInCurrency = refundedInCurrency;
           this.refundedAmount = refundedAmount;
           this.validationOnly = validationOnly;
           this.resultCode = resultCode;
           this.errorCode = errorCode;
           this.errorCodeDescription = errorCodeDescription;
    }


    /**
     * Gets the rtid value for this RefundRequestReturnType.
     * 
     * @return rtid
     */
    public java.lang.String getRtid() {
        return rtid;
    }


    /**
     * Sets the rtid value for this RefundRequestReturnType.
     * 
     * @param rtid
     */
    public void setRtid(java.lang.String rtid) {
        this.rtid = rtid;
    }


    /**
     * Gets the mtid value for this RefundRequestReturnType.
     * 
     * @return mtid
     */
    public java.lang.String getMtid() {
        return mtid;
    }


    /**
     * Sets the mtid value for this RefundRequestReturnType.
     * 
     * @param mtid
     */
    public void setMtid(java.lang.String mtid) {
        this.mtid = mtid;
    }


    /**
     * Gets the requestedCurrency value for this RefundRequestReturnType.
     * 
     * @return requestedCurrency
     */
    public java.lang.String getRequestedCurrency() {
        return requestedCurrency;
    }


    /**
     * Sets the requestedCurrency value for this RefundRequestReturnType.
     * 
     * @param requestedCurrency
     */
    public void setRequestedCurrency(java.lang.String requestedCurrency) {
        this.requestedCurrency = requestedCurrency;
    }


    /**
     * Gets the requestedAmount value for this RefundRequestReturnType.
     * 
     * @return requestedAmount
     */
    public double getRequestedAmount() {
        return requestedAmount;
    }


    /**
     * Sets the requestedAmount value for this RefundRequestReturnType.
     * 
     * @param requestedAmount
     */
    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }


    /**
     * Gets the refundedInCurrency value for this RefundRequestReturnType.
     * 
     * @return refundedInCurrency
     */
    public java.lang.String getRefundedInCurrency() {
        return refundedInCurrency;
    }


    /**
     * Sets the refundedInCurrency value for this RefundRequestReturnType.
     * 
     * @param refundedInCurrency
     */
    public void setRefundedInCurrency(java.lang.String refundedInCurrency) {
        this.refundedInCurrency = refundedInCurrency;
    }


    /**
     * Gets the refundedAmount value for this RefundRequestReturnType.
     * 
     * @return refundedAmount
     */
    public java.lang.Double getRefundedAmount() {
        return refundedAmount;
    }


    /**
     * Sets the refundedAmount value for this RefundRequestReturnType.
     * 
     * @param refundedAmount
     */
    public void setRefundedAmount(java.lang.Double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }


    /**
     * Gets the validationOnly value for this RefundRequestReturnType.
     * 
     * @return validationOnly
     */
    public java.lang.Boolean getValidationOnly() {
        return validationOnly;
    }


    /**
     * Sets the validationOnly value for this RefundRequestReturnType.
     * 
     * @param validationOnly
     */
    public void setValidationOnly(java.lang.Boolean validationOnly) {
        this.validationOnly = validationOnly;
    }


    /**
     * Gets the resultCode value for this RefundRequestReturnType.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this RefundRequestReturnType.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this RefundRequestReturnType.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this RefundRequestReturnType.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorCodeDescription value for this RefundRequestReturnType.
     * 
     * @return errorCodeDescription
     */
    public java.lang.String getErrorCodeDescription() {
        return errorCodeDescription;
    }


    /**
     * Sets the errorCodeDescription value for this RefundRequestReturnType.
     * 
     * @param errorCodeDescription
     */
    public void setErrorCodeDescription(java.lang.String errorCodeDescription) {
        this.errorCodeDescription = errorCodeDescription;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RefundRequestReturnType)) return false;
        RefundRequestReturnType other = (RefundRequestReturnType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rtid==null && other.getRtid()==null) || 
             (this.rtid!=null &&
              this.rtid.equals(other.getRtid()))) &&
            ((this.mtid==null && other.getMtid()==null) || 
             (this.mtid!=null &&
              this.mtid.equals(other.getMtid()))) &&
            ((this.requestedCurrency==null && other.getRequestedCurrency()==null) || 
             (this.requestedCurrency!=null &&
              this.requestedCurrency.equals(other.getRequestedCurrency()))) &&
            this.requestedAmount == other.getRequestedAmount() &&
            ((this.refundedInCurrency==null && other.getRefundedInCurrency()==null) || 
             (this.refundedInCurrency!=null &&
              this.refundedInCurrency.equals(other.getRefundedInCurrency()))) &&
            ((this.refundedAmount==null && other.getRefundedAmount()==null) || 
             (this.refundedAmount!=null &&
              this.refundedAmount.equals(other.getRefundedAmount()))) &&
            ((this.validationOnly==null && other.getValidationOnly()==null) || 
             (this.validationOnly!=null &&
              this.validationOnly.equals(other.getValidationOnly()))) &&
            this.resultCode == other.getResultCode() &&
            this.errorCode == other.getErrorCode() &&
            ((this.errorCodeDescription==null && other.getErrorCodeDescription()==null) || 
             (this.errorCodeDescription!=null &&
              this.errorCodeDescription.equals(other.getErrorCodeDescription())));
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
        if (getRtid() != null) {
            _hashCode += getRtid().hashCode();
        }
        if (getMtid() != null) {
            _hashCode += getMtid().hashCode();
        }
        if (getRequestedCurrency() != null) {
            _hashCode += getRequestedCurrency().hashCode();
        }
        _hashCode += new Double(getRequestedAmount()).hashCode();
        if (getRefundedInCurrency() != null) {
            _hashCode += getRefundedInCurrency().hashCode();
        }
        if (getRefundedAmount() != null) {
            _hashCode += getRefundedAmount().hashCode();
        }
        if (getValidationOnly() != null) {
            _hashCode += getValidationOnly().hashCode();
        }
        _hashCode += getResultCode();
        _hashCode += getErrorCode();
        if (getErrorCodeDescription() != null) {
            _hashCode += getErrorCodeDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RefundRequestReturnType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "RefundRequestReturnType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rtid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "rtid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mtid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mtid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "requestedCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "requestedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refundedInCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "refundedInCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refundedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "refundedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validationOnly");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "validationOnly"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "resultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCodeDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCodeDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

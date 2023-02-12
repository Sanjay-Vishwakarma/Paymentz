/**
 * PayoutReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class PayoutReturn  implements java.io.Serializable {
    private java.lang.String ptid;

    private java.lang.String requestedCurrency;

    private double requestedAmount;

    private java.lang.String payedInCurrency;

    private java.lang.Double payedAmount;

    private java.lang.Boolean validationOnly;

    private int resultCode;

    private int errorCode;

    private java.lang.String errorCodeDescription;

    public PayoutReturn() {
    }

    public PayoutReturn(
           java.lang.String ptid,
           java.lang.String requestedCurrency,
           double requestedAmount,
           java.lang.String payedInCurrency,
           java.lang.Double payedAmount,
           java.lang.Boolean validationOnly,
           int resultCode,
           int errorCode,
           java.lang.String errorCodeDescription) {
           this.ptid = ptid;
           this.requestedCurrency = requestedCurrency;
           this.requestedAmount = requestedAmount;
           this.payedInCurrency = payedInCurrency;
           this.payedAmount = payedAmount;
           this.validationOnly = validationOnly;
           this.resultCode = resultCode;
           this.errorCode = errorCode;
           this.errorCodeDescription = errorCodeDescription;
    }


    /**
     * Gets the ptid value for this PayoutReturn.
     * 
     * @return ptid
     */
    public java.lang.String getPtid() {
        return ptid;
    }


    /**
     * Sets the ptid value for this PayoutReturn.
     * 
     * @param ptid
     */
    public void setPtid(java.lang.String ptid) {
        this.ptid = ptid;
    }


    /**
     * Gets the requestedCurrency value for this PayoutReturn.
     * 
     * @return requestedCurrency
     */
    public java.lang.String getRequestedCurrency() {
        return requestedCurrency;
    }


    /**
     * Sets the requestedCurrency value for this PayoutReturn.
     * 
     * @param requestedCurrency
     */
    public void setRequestedCurrency(java.lang.String requestedCurrency) {
        this.requestedCurrency = requestedCurrency;
    }


    /**
     * Gets the requestedAmount value for this PayoutReturn.
     * 
     * @return requestedAmount
     */
    public double getRequestedAmount() {
        return requestedAmount;
    }


    /**
     * Sets the requestedAmount value for this PayoutReturn.
     * 
     * @param requestedAmount
     */
    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }


    /**
     * Gets the payedInCurrency value for this PayoutReturn.
     * 
     * @return payedInCurrency
     */
    public java.lang.String getPayedInCurrency() {
        return payedInCurrency;
    }


    /**
     * Sets the payedInCurrency value for this PayoutReturn.
     * 
     * @param payedInCurrency
     */
    public void setPayedInCurrency(java.lang.String payedInCurrency) {
        this.payedInCurrency = payedInCurrency;
    }


    /**
     * Gets the payedAmount value for this PayoutReturn.
     * 
     * @return payedAmount
     */
    public java.lang.Double getPayedAmount() {
        return payedAmount;
    }


    /**
     * Sets the payedAmount value for this PayoutReturn.
     * 
     * @param payedAmount
     */
    public void setPayedAmount(java.lang.Double payedAmount) {
        this.payedAmount = payedAmount;
    }


    /**
     * Gets the validationOnly value for this PayoutReturn.
     * 
     * @return validationOnly
     */
    public java.lang.Boolean getValidationOnly() {
        return validationOnly;
    }


    /**
     * Sets the validationOnly value for this PayoutReturn.
     * 
     * @param validationOnly
     */
    public void setValidationOnly(java.lang.Boolean validationOnly) {
        this.validationOnly = validationOnly;
    }


    /**
     * Gets the resultCode value for this PayoutReturn.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this PayoutReturn.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this PayoutReturn.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayoutReturn.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorCodeDescription value for this PayoutReturn.
     * 
     * @return errorCodeDescription
     */
    public java.lang.String getErrorCodeDescription() {
        return errorCodeDescription;
    }


    /**
     * Sets the errorCodeDescription value for this PayoutReturn.
     * 
     * @param errorCodeDescription
     */
    public void setErrorCodeDescription(java.lang.String errorCodeDescription) {
        this.errorCodeDescription = errorCodeDescription;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayoutReturn)) return false;
        PayoutReturn other = (PayoutReturn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ptid==null && other.getPtid()==null) || 
             (this.ptid!=null &&
              this.ptid.equals(other.getPtid()))) &&
            ((this.requestedCurrency==null && other.getRequestedCurrency()==null) || 
             (this.requestedCurrency!=null &&
              this.requestedCurrency.equals(other.getRequestedCurrency()))) &&
            this.requestedAmount == other.getRequestedAmount() &&
            ((this.payedInCurrency==null && other.getPayedInCurrency()==null) || 
             (this.payedInCurrency!=null &&
              this.payedInCurrency.equals(other.getPayedInCurrency()))) &&
            ((this.payedAmount==null && other.getPayedAmount()==null) || 
             (this.payedAmount!=null &&
              this.payedAmount.equals(other.getPayedAmount()))) &&
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
        if (getPtid() != null) {
            _hashCode += getPtid().hashCode();
        }
        if (getRequestedCurrency() != null) {
            _hashCode += getRequestedCurrency().hashCode();
        }
        _hashCode += new Double(getRequestedAmount()).hashCode();
        if (getPayedInCurrency() != null) {
            _hashCode += getPayedInCurrency().hashCode();
        }
        if (getPayedAmount() != null) {
            _hashCode += getPayedAmount().hashCode();
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
        new org.apache.axis.description.TypeDesc(PayoutReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "PayoutReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ptid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "ptid"));
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
        elemField.setFieldName("payedInCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "payedInCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "payedAmount"));
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

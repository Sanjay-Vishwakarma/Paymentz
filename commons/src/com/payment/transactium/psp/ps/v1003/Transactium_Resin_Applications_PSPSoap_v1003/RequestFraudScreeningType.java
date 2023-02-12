/**
 * RequestFraudScreeningType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestFraudScreeningType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestFraudScreeningType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.FraudScreeningType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxMindRiskScoreBlockThreshold");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MaxMindRiskScoreBlockThreshold"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxMindRiskScoreDeferThreshold");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MaxMindRiskScoreDeferThreshold"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private java.math.BigDecimal maxMindRiskScoreBlockThreshold;
    private java.math.BigDecimal maxMindRiskScoreDeferThreshold;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestFraudScreeningType() {
    }


    public RequestFraudScreeningType(
           java.math.BigDecimal maxMindRiskScoreBlockThreshold,
           java.math.BigDecimal maxMindRiskScoreDeferThreshold) {
           this.maxMindRiskScoreBlockThreshold = maxMindRiskScoreBlockThreshold;
           this.maxMindRiskScoreDeferThreshold = maxMindRiskScoreDeferThreshold;
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

    /**
     * Gets the maxMindRiskScoreBlockThreshold value for this RequestFraudScreeningType.
     *
     * @return maxMindRiskScoreBlockThreshold
     */
    public java.math.BigDecimal getMaxMindRiskScoreBlockThreshold() {
        return maxMindRiskScoreBlockThreshold;
    }

    /**
     * Sets the maxMindRiskScoreBlockThreshold value for this RequestFraudScreeningType.
     *
     * @param maxMindRiskScoreBlockThreshold
     */
    public void setMaxMindRiskScoreBlockThreshold(java.math.BigDecimal maxMindRiskScoreBlockThreshold) {
        this.maxMindRiskScoreBlockThreshold = maxMindRiskScoreBlockThreshold;
    }

    /**
     * Gets the maxMindRiskScoreDeferThreshold value for this RequestFraudScreeningType.
     *
     * @return maxMindRiskScoreDeferThreshold
     */
    public java.math.BigDecimal getMaxMindRiskScoreDeferThreshold() {
        return maxMindRiskScoreDeferThreshold;
    }

    /**
     * Sets the maxMindRiskScoreDeferThreshold value for this RequestFraudScreeningType.
     *
     * @param maxMindRiskScoreDeferThreshold
     */
    public void setMaxMindRiskScoreDeferThreshold(java.math.BigDecimal maxMindRiskScoreDeferThreshold) {
        this.maxMindRiskScoreDeferThreshold = maxMindRiskScoreDeferThreshold;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestFraudScreeningType)) return false;
        RequestFraudScreeningType other = (RequestFraudScreeningType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.maxMindRiskScoreBlockThreshold==null && other.getMaxMindRiskScoreBlockThreshold()==null) ||
             (this.maxMindRiskScoreBlockThreshold!=null &&
              this.maxMindRiskScoreBlockThreshold.equals(other.getMaxMindRiskScoreBlockThreshold()))) &&
            ((this.maxMindRiskScoreDeferThreshold==null && other.getMaxMindRiskScoreDeferThreshold()==null) ||
             (this.maxMindRiskScoreDeferThreshold!=null &&
              this.maxMindRiskScoreDeferThreshold.equals(other.getMaxMindRiskScoreDeferThreshold())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMaxMindRiskScoreBlockThreshold() != null) {
            _hashCode += getMaxMindRiskScoreBlockThreshold().hashCode();
        }
        if (getMaxMindRiskScoreDeferThreshold() != null) {
            _hashCode += getMaxMindRiskScoreDeferThreshold().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

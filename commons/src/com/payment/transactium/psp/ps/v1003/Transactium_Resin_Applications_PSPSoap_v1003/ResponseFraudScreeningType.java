/**
 * ResponseFraudScreeningType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseFraudScreeningType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseFraudScreeningType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.FraudScreeningType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("BINCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BINCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockReason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BlockReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IPCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "IPCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("intBlockReason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "IntBlockReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
    private String BINCountryCode;
    private String[] blockReason;
    private String IPCountryCode;
    private Long intBlockReason;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseFraudScreeningType() {
    }


    public ResponseFraudScreeningType(
           String BINCountryCode,
           String[] blockReason,
           String IPCountryCode,
           Long intBlockReason) {
           this.BINCountryCode = BINCountryCode;
           this.blockReason = blockReason;
           this.IPCountryCode = IPCountryCode;
           this.intBlockReason = intBlockReason;
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
     * Gets the BINCountryCode value for this ResponseFraudScreeningType.
     *
     * @return BINCountryCode
     */
    public String getBINCountryCode() {
        return BINCountryCode;
    }

    /**
     * Sets the BINCountryCode value for this ResponseFraudScreeningType.
     *
     * @param BINCountryCode
     */
    public void setBINCountryCode(String BINCountryCode) {
        this.BINCountryCode = BINCountryCode;
    }

    /**
     * Gets the blockReason value for this ResponseFraudScreeningType.
     *
     * @return blockReason
     */
    public String[] getBlockReason() {
        return blockReason;
    }

    /**
     * Sets the blockReason value for this ResponseFraudScreeningType.
     *
     * @param blockReason
     */
    public void setBlockReason(String[] blockReason) {
        this.blockReason = blockReason;
    }

    /**
     * Gets the IPCountryCode value for this ResponseFraudScreeningType.
     *
     * @return IPCountryCode
     */
    public String getIPCountryCode() {
        return IPCountryCode;
    }

    /**
     * Sets the IPCountryCode value for this ResponseFraudScreeningType.
     *
     * @param IPCountryCode
     */
    public void setIPCountryCode(String IPCountryCode) {
        this.IPCountryCode = IPCountryCode;
    }

    /**
     * Gets the intBlockReason value for this ResponseFraudScreeningType.
     *
     * @return intBlockReason
     */
    public Long getIntBlockReason() {
        return intBlockReason;
    }

    /**
     * Sets the intBlockReason value for this ResponseFraudScreeningType.
     *
     * @param intBlockReason
     */
    public void setIntBlockReason(Long intBlockReason) {
        this.intBlockReason = intBlockReason;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseFraudScreeningType)) return false;
        ResponseFraudScreeningType other = (ResponseFraudScreeningType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.BINCountryCode==null && other.getBINCountryCode()==null) ||
             (this.BINCountryCode!=null &&
              this.BINCountryCode.equals(other.getBINCountryCode()))) &&
            ((this.blockReason==null && other.getBlockReason()==null) ||
             (this.blockReason!=null &&
              java.util.Arrays.equals(this.blockReason, other.getBlockReason()))) &&
            ((this.IPCountryCode==null && other.getIPCountryCode()==null) ||
             (this.IPCountryCode!=null &&
              this.IPCountryCode.equals(other.getIPCountryCode()))) &&
            ((this.intBlockReason==null && other.getIntBlockReason()==null) ||
             (this.intBlockReason!=null &&
              this.intBlockReason.equals(other.getIntBlockReason())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getBINCountryCode() != null) {
            _hashCode += getBINCountryCode().hashCode();
        }
        if (getBlockReason() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBlockReason());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getBlockReason(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIPCountryCode() != null) {
            _hashCode += getIPCountryCode().hashCode();
        }
        if (getIntBlockReason() != null) {
            _hashCode += getIntBlockReason().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

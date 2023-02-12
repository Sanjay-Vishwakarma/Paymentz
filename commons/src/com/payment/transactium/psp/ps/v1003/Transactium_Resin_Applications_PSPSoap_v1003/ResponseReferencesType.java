/**
 * ResponseReferencesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseReferencesType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseReferencesType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.ReferencesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ARN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ARN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Merchant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String ARN;
    private String merchant;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseReferencesType() {
    }


    public ResponseReferencesType(
           String ARN,
           String merchant) {
           this.ARN = ARN;
           this.merchant = merchant;
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
     * Gets the ARN value for this ResponseReferencesType.
     *
     * @return ARN
     */
    public String getARN() {
        return ARN;
    }

    /**
     * Sets the ARN value for this ResponseReferencesType.
     *
     * @param ARN
     */
    public void setARN(String ARN) {
        this.ARN = ARN;
    }

    /**
     * Gets the merchant value for this ResponseReferencesType.
     *
     * @return merchant
     */
    public String getMerchant() {
        return merchant;
    }

    /**
     * Sets the merchant value for this ResponseReferencesType.
     *
     * @param merchant
     */
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseReferencesType)) return false;
        ResponseReferencesType other = (ResponseReferencesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.ARN==null && other.getARN()==null) ||
             (this.ARN!=null &&
              this.ARN.equals(other.getARN()))) &&
            ((this.merchant==null && other.getMerchant()==null) ||
             (this.merchant!=null &&
              this.merchant.equals(other.getMerchant())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getARN() != null) {
            _hashCode += getARN().hashCode();
        }
        if (getMerchant() != null) {
            _hashCode += getMerchant().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

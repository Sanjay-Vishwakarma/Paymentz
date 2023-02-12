/**
 * ResponseThreeDSType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseThreeDSType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseThreeDSType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.ThreeDSType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ECI");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ECI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enrolled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Enrolled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.ThreeDSType.EnrolledStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PAReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PAReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("redirectURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "RedirectURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private org.apache.axis.types.UnsignedByte ECI;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseThreeDSTypeEnrolledStatus enrolled;
    private byte[] PAReq;
    private String redirectURL;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseThreeDSType() {
    }


    public ResponseThreeDSType(
           org.apache.axis.types.UnsignedByte ECI,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseThreeDSTypeEnrolledStatus enrolled,
           byte[] PAReq,
           String redirectURL) {
           this.ECI = ECI;
           this.enrolled = enrolled;
           this.PAReq = PAReq;
           this.redirectURL = redirectURL;
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
     * Gets the ECI value for this ResponseThreeDSType.
     *
     * @return ECI
     */
    public org.apache.axis.types.UnsignedByte getECI() {
        return ECI;
    }

    /**
     * Sets the ECI value for this ResponseThreeDSType.
     *
     * @param ECI
     */
    public void setECI(org.apache.axis.types.UnsignedByte ECI) {
        this.ECI = ECI;
    }

    /**
     * Gets the enrolled value for this ResponseThreeDSType.
     *
     * @return enrolled
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseThreeDSTypeEnrolledStatus getEnrolled() {
        return enrolled;
    }

    /**
     * Sets the enrolled value for this ResponseThreeDSType.
     *
     * @param enrolled
     */
    public void setEnrolled(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseThreeDSTypeEnrolledStatus enrolled) {
        this.enrolled = enrolled;
    }

    /**
     * Gets the PAReq value for this ResponseThreeDSType.
     *
     * @return PAReq
     */
    public byte[] getPAReq() {
        return PAReq;
    }

    /**
     * Sets the PAReq value for this ResponseThreeDSType.
     *
     * @param PAReq
     */
    public void setPAReq(byte[] PAReq) {
        this.PAReq = PAReq;
    }

    /**
     * Gets the redirectURL value for this ResponseThreeDSType.
     *
     * @return redirectURL
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * Sets the redirectURL value for this ResponseThreeDSType.
     *
     * @param redirectURL
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseThreeDSType)) return false;
        ResponseThreeDSType other = (ResponseThreeDSType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.ECI==null && other.getECI()==null) ||
             (this.ECI!=null &&
              this.ECI.equals(other.getECI()))) &&
            ((this.enrolled==null && other.getEnrolled()==null) ||
             (this.enrolled!=null &&
              this.enrolled.equals(other.getEnrolled()))) &&
            ((this.PAReq==null && other.getPAReq()==null) ||
             (this.PAReq!=null &&
              java.util.Arrays.equals(this.PAReq, other.getPAReq()))) &&
            ((this.redirectURL==null && other.getRedirectURL()==null) ||
             (this.redirectURL!=null &&
              this.redirectURL.equals(other.getRedirectURL())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getECI() != null) {
            _hashCode += getECI().hashCode();
        }
        if (getEnrolled() != null) {
            _hashCode += getEnrolled().hashCode();
        }
        if (getPAReq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPAReq());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPAReq(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRedirectURL() != null) {
            _hashCode += getRedirectURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

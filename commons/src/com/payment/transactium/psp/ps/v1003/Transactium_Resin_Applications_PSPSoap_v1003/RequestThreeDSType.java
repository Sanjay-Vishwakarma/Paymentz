/**
 * RequestThreeDSType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestThreeDSType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestThreeDSType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.ThreeDSType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAVV");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CAVV"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ECI");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ECI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PARes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PARes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "XID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private byte[] CAVV;
    private org.apache.axis.types.UnsignedByte ECI;
    private byte[] PARes;
    private byte[] XID;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestThreeDSType() {
    }


    public RequestThreeDSType(
           byte[] CAVV,
           org.apache.axis.types.UnsignedByte ECI,
           byte[] PARes,
           byte[] XID) {
           this.CAVV = CAVV;
           this.ECI = ECI;
           this.PARes = PARes;
           this.XID = XID;
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
     * Gets the CAVV value for this RequestThreeDSType.
     *
     * @return CAVV
     */
    public byte[] getCAVV() {
        return CAVV;
    }

    /**
     * Sets the CAVV value for this RequestThreeDSType.
     *
     * @param CAVV
     */
    public void setCAVV(byte[] CAVV) {
        this.CAVV = CAVV;
    }

    /**
     * Gets the ECI value for this RequestThreeDSType.
     *
     * @return ECI
     */
    public org.apache.axis.types.UnsignedByte getECI() {
        return ECI;
    }

    /**
     * Sets the ECI value for this RequestThreeDSType.
     *
     * @param ECI
     */
    public void setECI(org.apache.axis.types.UnsignedByte ECI) {
        this.ECI = ECI;
    }

    /**
     * Gets the PARes value for this RequestThreeDSType.
     *
     * @return PARes
     */
    public byte[] getPARes() {
        return PARes;
    }

    /**
     * Sets the PARes value for this RequestThreeDSType.
     *
     * @param PARes
     */
    public void setPARes(byte[] PARes) {
        this.PARes = PARes;
    }

    /**
     * Gets the XID value for this RequestThreeDSType.
     *
     * @return XID
     */
    public byte[] getXID() {
        return XID;
    }

    /**
     * Sets the XID value for this RequestThreeDSType.
     *
     * @param XID
     */
    public void setXID(byte[] XID) {
        this.XID = XID;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestThreeDSType)) return false;
        RequestThreeDSType other = (RequestThreeDSType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.CAVV==null && other.getCAVV()==null) ||
             (this.CAVV!=null &&
              java.util.Arrays.equals(this.CAVV, other.getCAVV()))) &&
            ((this.ECI==null && other.getECI()==null) ||
             (this.ECI!=null &&
              this.ECI.equals(other.getECI()))) &&
            ((this.PARes==null && other.getPARes()==null) ||
             (this.PARes!=null &&
              java.util.Arrays.equals(this.PARes, other.getPARes()))) &&
            ((this.XID==null && other.getXID()==null) ||
             (this.XID!=null &&
              java.util.Arrays.equals(this.XID, other.getXID())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCAVV() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCAVV());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getCAVV(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getECI() != null) {
            _hashCode += getECI().hashCode();
        }
        if (getPARes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPARes());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPARes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getXID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getXID());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getXID(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

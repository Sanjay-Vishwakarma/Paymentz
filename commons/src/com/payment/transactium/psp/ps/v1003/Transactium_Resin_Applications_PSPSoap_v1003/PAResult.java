/**
 * PAResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class PAResult  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PAResult.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PAResult"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PAResult.StatusCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "XID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private byte[] CAVV;
    private String ECI;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.PAResultStatusCode status;
    private String XID;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public PAResult() {
    }


    public PAResult(
           byte[] CAVV,
           String ECI,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.PAResultStatusCode status,
           String XID) {
           this.CAVV = CAVV;
           this.ECI = ECI;
           this.status = status;
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
     * Gets the CAVV value for this PAResult.
     *
     * @return CAVV
     */
    public byte[] getCAVV() {
        return CAVV;
    }

    /**
     * Sets the CAVV value for this PAResult.
     *
     * @param CAVV
     */
    public void setCAVV(byte[] CAVV) {
        this.CAVV = CAVV;
    }

    /**
     * Gets the ECI value for this PAResult.
     *
     * @return ECI
     */
    public String getECI() {
        return ECI;
    }

    /**
     * Sets the ECI value for this PAResult.
     *
     * @param ECI
     */
    public void setECI(String ECI) {
        this.ECI = ECI;
    }

    /**
     * Gets the status value for this PAResult.
     *
     * @return status
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.PAResultStatusCode getStatus() {
        return status;
    }

    /**
     * Sets the status value for this PAResult.
     *
     * @param status
     */
    public void setStatus(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.PAResultStatusCode status) {
        this.status = status;
    }

    /**
     * Gets the XID value for this PAResult.
     *
     * @return XID
     */
    public String getXID() {
        return XID;
    }

    /**
     * Sets the XID value for this PAResult.
     *
     * @param XID
     */
    public void setXID(String XID) {
        this.XID = XID;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof PAResult)) return false;
        PAResult other = (PAResult) obj;
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
            ((this.status==null && other.getStatus()==null) ||
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.XID==null && other.getXID()==null) ||
             (this.XID!=null &&
              this.XID.equals(other.getXID())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getXID() != null) {
            _hashCode += getXID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

/**
 * MPIPARequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class MPIPARequest  extends com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequest  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MPIPARequest.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MPIPARequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ExternalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PARes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PARes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String externalID;
    private String PARes;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public MPIPARequest() {
    }


    public MPIPARequest(
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestCardType card,
           String token,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestTransactionType transaction,
           String externalID,
           String PARes) {
        super(
            authentication,
            card,
            token,
            transaction);
        this.externalID = externalID;
        this.PARes = PARes;
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
     * Gets the externalID value for this MPIPARequest.
     *
     * @return externalID
     */
    public String getExternalID() {
        return externalID;
    }

    /**
     * Sets the externalID value for this MPIPARequest.
     *
     * @param externalID
     */
    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    /**
     * Gets the PARes value for this MPIPARequest.
     *
     * @return PARes
     */
    public String getPARes() {
        return PARes;
    }

    /**
     * Sets the PARes value for this MPIPARequest.
     *
     * @param PARes
     */
    public void setPARes(String PARes) {
        this.PARes = PARes;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MPIPARequest)) return false;
        MPIPARequest other = (MPIPARequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
            ((this.externalID==null && other.getExternalID()==null) ||
             (this.externalID!=null &&
              this.externalID.equals(other.getExternalID()))) &&
            ((this.PARes==null && other.getPARes()==null) ||
             (this.PARes!=null &&
              this.PARes.equals(other.getPARes())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getExternalID() != null) {
            _hashCode += getExternalID().hashCode();
        }
        if (getPARes() != null) {
            _hashCode += getPARes().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

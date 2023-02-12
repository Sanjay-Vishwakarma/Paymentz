/**
 * RequestAuthenticationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestAuthenticationType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestAuthenticationType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.AuthenticationType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("businessUnitId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BusinessUnitId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entityId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "EntityId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("impersonatedAPIUserId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ImpersonatedAPIUserId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portalUserId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PortalUserId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Signature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "UserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private org.apache.axis.types.UnsignedInt businessUnitId;
    private org.apache.axis.types.UnsignedInt entityId;
    private org.apache.axis.types.UnsignedInt impersonatedAPIUserId;
    private String password;
    private org.apache.axis.types.UnsignedInt portalUserId;
    private byte[] signature;
    private String userName;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestAuthenticationType() {
    }


    public RequestAuthenticationType(
           org.apache.axis.types.UnsignedInt businessUnitId,
           org.apache.axis.types.UnsignedInt entityId,
           org.apache.axis.types.UnsignedInt impersonatedAPIUserId,
           String password,
           org.apache.axis.types.UnsignedInt portalUserId,
           byte[] signature,
           String userName) {
           this.businessUnitId = businessUnitId;
           this.entityId = entityId;
           this.impersonatedAPIUserId = impersonatedAPIUserId;
           this.password = password;
           this.portalUserId = portalUserId;
           this.signature = signature;
           this.userName = userName;
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
     * Gets the businessUnitId value for this RequestAuthenticationType.
     *
     * @return businessUnitId
     */
    public org.apache.axis.types.UnsignedInt getBusinessUnitId() {
        return businessUnitId;
    }

    /**
     * Sets the businessUnitId value for this RequestAuthenticationType.
     *
     * @param businessUnitId
     */
    public void setBusinessUnitId(org.apache.axis.types.UnsignedInt businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    /**
     * Gets the entityId value for this RequestAuthenticationType.
     *
     * @return entityId
     */
    public org.apache.axis.types.UnsignedInt getEntityId() {
        return entityId;
    }

    /**
     * Sets the entityId value for this RequestAuthenticationType.
     *
     * @param entityId
     */
    public void setEntityId(org.apache.axis.types.UnsignedInt entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the impersonatedAPIUserId value for this RequestAuthenticationType.
     *
     * @return impersonatedAPIUserId
     */
    public org.apache.axis.types.UnsignedInt getImpersonatedAPIUserId() {
        return impersonatedAPIUserId;
    }

    /**
     * Sets the impersonatedAPIUserId value for this RequestAuthenticationType.
     *
     * @param impersonatedAPIUserId
     */
    public void setImpersonatedAPIUserId(org.apache.axis.types.UnsignedInt impersonatedAPIUserId) {
        this.impersonatedAPIUserId = impersonatedAPIUserId;
    }

    /**
     * Gets the password value for this RequestAuthenticationType.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password value for this RequestAuthenticationType.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the portalUserId value for this RequestAuthenticationType.
     *
     * @return portalUserId
     */
    public org.apache.axis.types.UnsignedInt getPortalUserId() {
        return portalUserId;
    }

    /**
     * Sets the portalUserId value for this RequestAuthenticationType.
     *
     * @param portalUserId
     */
    public void setPortalUserId(org.apache.axis.types.UnsignedInt portalUserId) {
        this.portalUserId = portalUserId;
    }

    /**
     * Gets the signature value for this RequestAuthenticationType.
     *
     * @return signature
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Sets the signature value for this RequestAuthenticationType.
     *
     * @param signature
     */
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    /**
     * Gets the userName value for this RequestAuthenticationType.
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the userName value for this RequestAuthenticationType.
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestAuthenticationType)) return false;
        RequestAuthenticationType other = (RequestAuthenticationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.businessUnitId==null && other.getBusinessUnitId()==null) ||
             (this.businessUnitId!=null &&
              this.businessUnitId.equals(other.getBusinessUnitId()))) &&
            ((this.entityId==null && other.getEntityId()==null) ||
             (this.entityId!=null &&
              this.entityId.equals(other.getEntityId()))) &&
            ((this.impersonatedAPIUserId==null && other.getImpersonatedAPIUserId()==null) ||
             (this.impersonatedAPIUserId!=null &&
              this.impersonatedAPIUserId.equals(other.getImpersonatedAPIUserId()))) &&
            ((this.password==null && other.getPassword()==null) ||
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.portalUserId==null && other.getPortalUserId()==null) ||
             (this.portalUserId!=null &&
              this.portalUserId.equals(other.getPortalUserId()))) &&
            ((this.signature==null && other.getSignature()==null) ||
             (this.signature!=null &&
              java.util.Arrays.equals(this.signature, other.getSignature()))) &&
            ((this.userName==null && other.getUserName()==null) ||
             (this.userName!=null &&
              this.userName.equals(other.getUserName())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getBusinessUnitId() != null) {
            _hashCode += getBusinessUnitId().hashCode();
        }
        if (getEntityId() != null) {
            _hashCode += getEntityId().hashCode();
        }
        if (getImpersonatedAPIUserId() != null) {
            _hashCode += getImpersonatedAPIUserId().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getPortalUserId() != null) {
            _hashCode += getPortalUserId().hashCode();
        }
        if (getSignature() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignature());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getSignature(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

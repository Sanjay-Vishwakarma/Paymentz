/**
 * ResponseCardType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseCardType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseCardType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.CardType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicationProfile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ApplicationProfile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Bin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardProfile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CardProfile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryYYMM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ExpiryYYMM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastFourDigits");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "LastFourDigits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String applicationProfile;
    private String bin;
    private String cardProfile;
    private String expiryYYMM;
    private String id;
    private String lastFourDigits;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseCardType() {
    }


    public ResponseCardType(
           String applicationProfile,
           String bin,
           String cardProfile,
           String expiryYYMM,
           String id,
           String lastFourDigits) {
           this.applicationProfile = applicationProfile;
           this.bin = bin;
           this.cardProfile = cardProfile;
           this.expiryYYMM = expiryYYMM;
           this.id = id;
           this.lastFourDigits = lastFourDigits;
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
     * Gets the applicationProfile value for this ResponseCardType.
     *
     * @return applicationProfile
     */
    public String getApplicationProfile() {
        return applicationProfile;
    }

    /**
     * Sets the applicationProfile value for this ResponseCardType.
     *
     * @param applicationProfile
     */
    public void setApplicationProfile(String applicationProfile) {
        this.applicationProfile = applicationProfile;
    }

    /**
     * Gets the bin value for this ResponseCardType.
     *
     * @return bin
     */
    public String getBin() {
        return bin;
    }

    /**
     * Sets the bin value for this ResponseCardType.
     *
     * @param bin
     */
    public void setBin(String bin) {
        this.bin = bin;
    }

    /**
     * Gets the cardProfile value for this ResponseCardType.
     *
     * @return cardProfile
     */
    public String getCardProfile() {
        return cardProfile;
    }

    /**
     * Sets the cardProfile value for this ResponseCardType.
     *
     * @param cardProfile
     */
    public void setCardProfile(String cardProfile) {
        this.cardProfile = cardProfile;
    }

    /**
     * Gets the expiryYYMM value for this ResponseCardType.
     *
     * @return expiryYYMM
     */
    public String getExpiryYYMM() {
        return expiryYYMM;
    }

    /**
     * Sets the expiryYYMM value for this ResponseCardType.
     *
     * @param expiryYYMM
     */
    public void setExpiryYYMM(String expiryYYMM) {
        this.expiryYYMM = expiryYYMM;
    }

    /**
     * Gets the id value for this ResponseCardType.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id value for this ResponseCardType.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the lastFourDigits value for this ResponseCardType.
     *
     * @return lastFourDigits
     */
    public String getLastFourDigits() {
        return lastFourDigits;
    }

    /**
     * Sets the lastFourDigits value for this ResponseCardType.
     *
     * @param lastFourDigits
     */
    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseCardType)) return false;
        ResponseCardType other = (ResponseCardType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.applicationProfile==null && other.getApplicationProfile()==null) ||
             (this.applicationProfile!=null &&
              this.applicationProfile.equals(other.getApplicationProfile()))) &&
            ((this.bin==null && other.getBin()==null) ||
             (this.bin!=null &&
              this.bin.equals(other.getBin()))) &&
            ((this.cardProfile==null && other.getCardProfile()==null) ||
             (this.cardProfile!=null &&
              this.cardProfile.equals(other.getCardProfile()))) &&
            ((this.expiryYYMM==null && other.getExpiryYYMM()==null) ||
             (this.expiryYYMM!=null &&
              this.expiryYYMM.equals(other.getExpiryYYMM()))) &&
            ((this.id==null && other.getId()==null) ||
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.lastFourDigits==null && other.getLastFourDigits()==null) ||
             (this.lastFourDigits!=null &&
              this.lastFourDigits.equals(other.getLastFourDigits())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getApplicationProfile() != null) {
            _hashCode += getApplicationProfile().hashCode();
        }
        if (getBin() != null) {
            _hashCode += getBin().hashCode();
        }
        if (getCardProfile() != null) {
            _hashCode += getCardProfile().hashCode();
        }
        if (getExpiryYYMM() != null) {
            _hashCode += getExpiryYYMM().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getLastFourDigits() != null) {
            _hashCode += getLastFourDigits().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

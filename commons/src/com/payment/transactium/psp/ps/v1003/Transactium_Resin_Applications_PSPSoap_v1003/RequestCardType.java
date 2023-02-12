/**
 * RequestCardType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestCardType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestCardType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.CardType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CVV2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CVV2"));
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
        elemField.setFieldName("holderName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HolderName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("number");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentInstrumentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PaymentInstrumentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String CVV2;
    private String expiryYYMM;
    private String holderName;
    private String number;
    private byte[] paymentInstrumentId;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestCardType() {
    }


    public RequestCardType(
           String CVV2,
           String expiryYYMM,
           String holderName,
           String number,
           byte[] paymentInstrumentId) {
           this.CVV2 = CVV2;
           this.expiryYYMM = expiryYYMM;
           this.holderName = holderName;
           this.number = number;
           this.paymentInstrumentId = paymentInstrumentId;
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
     * Gets the CVV2 value for this RequestCardType.
     *
     * @return CVV2
     */
    public String getCVV2() {
        return CVV2;
    }

    /**
     * Sets the CVV2 value for this RequestCardType.
     *
     * @param CVV2
     */
    public void setCVV2(String CVV2) {
        this.CVV2 = CVV2;
    }

    /**
     * Gets the expiryYYMM value for this RequestCardType.
     *
     * @return expiryYYMM
     */
    public String getExpiryYYMM() {
        return expiryYYMM;
    }

    /**
     * Sets the expiryYYMM value for this RequestCardType.
     *
     * @param expiryYYMM
     */
    public void setExpiryYYMM(String expiryYYMM) {
        this.expiryYYMM = expiryYYMM;
    }

    /**
     * Gets the holderName value for this RequestCardType.
     *
     * @return holderName
     */
    public String getHolderName() {
        return holderName;
    }

    /**
     * Sets the holderName value for this RequestCardType.
     *
     * @param holderName
     */
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    /**
     * Gets the number value for this RequestCardType.
     *
     * @return number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number value for this RequestCardType.
     *
     * @param number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the paymentInstrumentId value for this RequestCardType.
     *
     * @return paymentInstrumentId
     */
    public byte[] getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    /**
     * Sets the paymentInstrumentId value for this RequestCardType.
     *
     * @param paymentInstrumentId
     */
    public void setPaymentInstrumentId(byte[] paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestCardType)) return false;
        RequestCardType other = (RequestCardType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.CVV2==null && other.getCVV2()==null) ||
             (this.CVV2!=null &&
              this.CVV2.equals(other.getCVV2()))) &&
            ((this.expiryYYMM==null && other.getExpiryYYMM()==null) ||
             (this.expiryYYMM!=null &&
              this.expiryYYMM.equals(other.getExpiryYYMM()))) &&
            ((this.holderName==null && other.getHolderName()==null) ||
             (this.holderName!=null &&
              this.holderName.equals(other.getHolderName()))) &&
            ((this.number==null && other.getNumber()==null) ||
             (this.number!=null &&
              this.number.equals(other.getNumber()))) &&
            ((this.paymentInstrumentId==null && other.getPaymentInstrumentId()==null) ||
             (this.paymentInstrumentId!=null &&
              java.util.Arrays.equals(this.paymentInstrumentId, other.getPaymentInstrumentId())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCVV2() != null) {
            _hashCode += getCVV2().hashCode();
        }
        if (getExpiryYYMM() != null) {
            _hashCode += getExpiryYYMM().hashCode();
        }
        if (getHolderName() != null) {
            _hashCode += getHolderName().hashCode();
        }
        if (getNumber() != null) {
            _hashCode += getNumber().hashCode();
        }
        if (getPaymentInstrumentId() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPaymentInstrumentId());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPaymentInstrumentId(), i);
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

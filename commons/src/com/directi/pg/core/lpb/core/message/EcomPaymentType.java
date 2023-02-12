/**
 * EcomPaymentType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public class EcomPaymentType  implements java.io.Serializable {
    private String INTERFACE;

    private java.math.BigInteger KEY_INDEX;

    private String KEY;

    private String DATA;

    private String SIGNATURE;

    public EcomPaymentType() {
    }

    public EcomPaymentType(
           String INTERFACE,
           java.math.BigInteger KEY_INDEX,
           String KEY,
           String DATA,
           String SIGNATURE) {
           this.INTERFACE = INTERFACE;
           this.KEY_INDEX = KEY_INDEX;
           this.KEY = KEY;
           this.DATA = DATA;
           this.SIGNATURE = SIGNATURE;
    }


    /**
     * Gets the INTERFACE value for this EcomPaymentType.
     * 
     * @return INTERFACE
     */
    public String getINTERFACE() {
        return INTERFACE;
    }


    /**
     * Sets the INTERFACE value for this EcomPaymentType.
     * 
     * @param INTERFACE
     */
    public void setINTERFACE(String INTERFACE) {
        this.INTERFACE = INTERFACE;
    }


    /**
     * Gets the KEY_INDEX value for this EcomPaymentType.
     * 
     * @return KEY_INDEX
     */
    public java.math.BigInteger getKEY_INDEX() {
        return KEY_INDEX;
    }


    /**
     * Sets the KEY_INDEX value for this EcomPaymentType.
     * 
     * @param KEY_INDEX
     */
    public void setKEY_INDEX(java.math.BigInteger KEY_INDEX) {
        this.KEY_INDEX = KEY_INDEX;
    }


    /**
     * Gets the KEY value for this EcomPaymentType.
     * 
     * @return KEY
     */
    public String getKEY() {
        return KEY;
    }


    /**
     * Sets the KEY value for this EcomPaymentType.
     * 
     * @param KEY
     */
    public void setKEY(String KEY) {
        this.KEY = KEY;
    }


    /**
     * Gets the DATA value for this EcomPaymentType.
     * 
     * @return DATA
     */
    public String getDATA() {
        return DATA;
    }


    /**
     * Sets the DATA value for this EcomPaymentType.
     * 
     * @param DATA
     */
    public void setDATA(String DATA) {
        this.DATA = DATA;
    }


    /**
     * Gets the SIGNATURE value for this EcomPaymentType.
     * 
     * @return SIGNATURE
     */
    public String getSIGNATURE() {
        return SIGNATURE;
    }


    /**
     * Sets the SIGNATURE value for this EcomPaymentType.
     * 
     * @param SIGNATURE
     */
    public void setSIGNATURE(String SIGNATURE) {
        this.SIGNATURE = SIGNATURE;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof EcomPaymentType)) return false;
        EcomPaymentType other = (EcomPaymentType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.INTERFACE==null && other.getINTERFACE()==null) || 
             (this.INTERFACE!=null &&
              this.INTERFACE.equals(other.getINTERFACE()))) &&
            ((this.KEY_INDEX==null && other.getKEY_INDEX()==null) || 
             (this.KEY_INDEX!=null &&
              this.KEY_INDEX.equals(other.getKEY_INDEX()))) &&
            ((this.KEY==null && other.getKEY()==null) || 
             (this.KEY!=null &&
              this.KEY.equals(other.getKEY()))) &&
            ((this.DATA==null && other.getDATA()==null) || 
             (this.DATA!=null &&
              this.DATA.equals(other.getDATA()))) &&
            ((this.SIGNATURE==null && other.getSIGNATURE()==null) || 
             (this.SIGNATURE!=null &&
              this.SIGNATURE.equals(other.getSIGNATURE())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getINTERFACE() != null) {
            _hashCode += getINTERFACE().hashCode();
        }
        if (getKEY_INDEX() != null) {
            _hashCode += getKEY_INDEX().hashCode();
        }
        if (getKEY() != null) {
            _hashCode += getKEY().hashCode();
        }
        if (getDATA() != null) {
            _hashCode += getDATA().hashCode();
        }
        if (getSIGNATURE() != null) {
            _hashCode += getSIGNATURE().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EcomPaymentType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Gateway", "ecomPaymentType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("INTERFACE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "INTERFACE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KEY_INDEX");
        elemField.setXmlName(new javax.xml.namespace.QName("", "KEY_INDEX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KEY");
        elemField.setXmlName(new javax.xml.namespace.QName("", "KEY"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DATA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DATA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SIGNATURE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SIGNATURE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
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

}

/**
 * MrsDATA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public class MrsDATA  implements java.io.Serializable {
    private String MERCHANT;

    private java.math.BigInteger INDEX;

    private String SIGN;

    private String KEY;

    private String DATA;

    private String CALLBACK;

    public MrsDATA() {
    }

    public MrsDATA(
           String MERCHANT,
           java.math.BigInteger INDEX,
           String SIGN,
           String KEY,
           String DATA,
           String CALLBACK) {
           this.MERCHANT = MERCHANT;
           this.INDEX = INDEX;
           this.SIGN = SIGN;
           this.KEY = KEY;
           this.DATA = DATA;
           this.CALLBACK = CALLBACK;
    }


    /**
     * Gets the MERCHANT value for this MrsDATA.
     * 
     * @return MERCHANT
     */
    public String getMERCHANT() {
        return MERCHANT;
    }


    /**
     * Sets the MERCHANT value for this MrsDATA.
     * 
     * @param MERCHANT
     */
    public void setMERCHANT(String MERCHANT) {
        this.MERCHANT = MERCHANT;
    }


    /**
     * Gets the INDEX value for this MrsDATA.
     * 
     * @return INDEX
     */
    public java.math.BigInteger getINDEX() {
        return INDEX;
    }


    /**
     * Sets the INDEX value for this MrsDATA.
     * 
     * @param INDEX
     */
    public void setINDEX(java.math.BigInteger INDEX) {
        this.INDEX = INDEX;
    }


    /**
     * Gets the SIGN value for this MrsDATA.
     * 
     * @return SIGN
     */
    public String getSIGN() {
        return SIGN;
    }


    /**
     * Sets the SIGN value for this MrsDATA.
     * 
     * @param SIGN
     */
    public void setSIGN(String SIGN) {
        this.SIGN = SIGN;
    }


    /**
     * Gets the KEY value for this MrsDATA.
     * 
     * @return KEY
     */
    public String getKEY() {
        return KEY;
    }


    /**
     * Sets the KEY value for this MrsDATA.
     * 
     * @param KEY
     */
    public void setKEY(String KEY) {
        this.KEY = KEY;
    }


    /**
     * Gets the DATA value for this MrsDATA.
     * 
     * @return DATA
     */
    public String getDATA() {
        return DATA;
    }


    /**
     * Sets the DATA value for this MrsDATA.
     * 
     * @param DATA
     */
    public void setDATA(String DATA) {
        this.DATA = DATA;
    }


    /**
     * Gets the CALLBACK value for this MrsDATA.
     * 
     * @return CALLBACK
     */
    public String getCALLBACK() {
        return CALLBACK;
    }


    /**
     * Sets the CALLBACK value for this MrsDATA.
     * 
     * @param CALLBACK
     */
    public void setCALLBACK(String CALLBACK) {
        this.CALLBACK = CALLBACK;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MrsDATA)) return false;
        MrsDATA other = (MrsDATA) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.MERCHANT==null && other.getMERCHANT()==null) || 
             (this.MERCHANT!=null &&
              this.MERCHANT.equals(other.getMERCHANT()))) &&
            ((this.INDEX==null && other.getINDEX()==null) || 
             (this.INDEX!=null &&
              this.INDEX.equals(other.getINDEX()))) &&
            ((this.SIGN==null && other.getSIGN()==null) || 
             (this.SIGN!=null &&
              this.SIGN.equals(other.getSIGN()))) &&
            ((this.KEY==null && other.getKEY()==null) || 
             (this.KEY!=null &&
              this.KEY.equals(other.getKEY()))) &&
            ((this.DATA==null && other.getDATA()==null) || 
             (this.DATA!=null &&
              this.DATA.equals(other.getDATA()))) &&
            ((this.CALLBACK==null && other.getCALLBACK()==null) || 
             (this.CALLBACK!=null &&
              this.CALLBACK.equals(other.getCALLBACK())));
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
        if (getMERCHANT() != null) {
            _hashCode += getMERCHANT().hashCode();
        }
        if (getINDEX() != null) {
            _hashCode += getINDEX().hashCode();
        }
        if (getSIGN() != null) {
            _hashCode += getSIGN().hashCode();
        }
        if (getKEY() != null) {
            _hashCode += getKEY().hashCode();
        }
        if (getDATA() != null) {
            _hashCode += getDATA().hashCode();
        }
        if (getCALLBACK() != null) {
            _hashCode += getCALLBACK().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MrsDATA.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Gateway", "mrsDATA"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MERCHANT");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MERCHANT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("INDEX");
        elemField.setXmlName(new javax.xml.namespace.QName("", "INDEX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SIGN");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SIGN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KEY");
        elemField.setXmlName(new javax.xml.namespace.QName("", "KEY"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DATA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DATA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CALLBACK");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CALLBACK"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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

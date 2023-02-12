/**
 * MerchantClientData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class MerchantClientData  implements java.io.Serializable {
    private java.lang.String value01;

    private java.lang.String value02;

    private java.lang.String value03;

    private java.lang.String value04;

    private java.lang.String value05;

    private java.lang.String value06;

    private java.lang.String value07;

    private java.lang.String value08;

    private java.lang.String value09;

    private java.lang.String value10;

    public MerchantClientData() {
    }

    public MerchantClientData(
           java.lang.String value01,
           java.lang.String value02,
           java.lang.String value03,
           java.lang.String value04,
           java.lang.String value05,
           java.lang.String value06,
           java.lang.String value07,
           java.lang.String value08,
           java.lang.String value09,
           java.lang.String value10) {
           this.value01 = value01;
           this.value02 = value02;
           this.value03 = value03;
           this.value04 = value04;
           this.value05 = value05;
           this.value06 = value06;
           this.value07 = value07;
           this.value08 = value08;
           this.value09 = value09;
           this.value10 = value10;
    }


    /**
     * Gets the value01 value for this MerchantClientData.
     * 
     * @return value01
     */
    public java.lang.String getValue01() {
        return value01;
    }


    /**
     * Sets the value01 value for this MerchantClientData.
     * 
     * @param value01
     */
    public void setValue01(java.lang.String value01) {
        this.value01 = value01;
    }


    /**
     * Gets the value02 value for this MerchantClientData.
     * 
     * @return value02
     */
    public java.lang.String getValue02() {
        return value02;
    }


    /**
     * Sets the value02 value for this MerchantClientData.
     * 
     * @param value02
     */
    public void setValue02(java.lang.String value02) {
        this.value02 = value02;
    }


    /**
     * Gets the value03 value for this MerchantClientData.
     * 
     * @return value03
     */
    public java.lang.String getValue03() {
        return value03;
    }


    /**
     * Sets the value03 value for this MerchantClientData.
     * 
     * @param value03
     */
    public void setValue03(java.lang.String value03) {
        this.value03 = value03;
    }


    /**
     * Gets the value04 value for this MerchantClientData.
     * 
     * @return value04
     */
    public java.lang.String getValue04() {
        return value04;
    }


    /**
     * Sets the value04 value for this MerchantClientData.
     * 
     * @param value04
     */
    public void setValue04(java.lang.String value04) {
        this.value04 = value04;
    }


    /**
     * Gets the value05 value for this MerchantClientData.
     * 
     * @return value05
     */
    public java.lang.String getValue05() {
        return value05;
    }


    /**
     * Sets the value05 value for this MerchantClientData.
     * 
     * @param value05
     */
    public void setValue05(java.lang.String value05) {
        this.value05 = value05;
    }


    /**
     * Gets the value06 value for this MerchantClientData.
     * 
     * @return value06
     */
    public java.lang.String getValue06() {
        return value06;
    }


    /**
     * Sets the value06 value for this MerchantClientData.
     * 
     * @param value06
     */
    public void setValue06(java.lang.String value06) {
        this.value06 = value06;
    }


    /**
     * Gets the value07 value for this MerchantClientData.
     * 
     * @return value07
     */
    public java.lang.String getValue07() {
        return value07;
    }


    /**
     * Sets the value07 value for this MerchantClientData.
     * 
     * @param value07
     */
    public void setValue07(java.lang.String value07) {
        this.value07 = value07;
    }


    /**
     * Gets the value08 value for this MerchantClientData.
     * 
     * @return value08
     */
    public java.lang.String getValue08() {
        return value08;
    }


    /**
     * Sets the value08 value for this MerchantClientData.
     * 
     * @param value08
     */
    public void setValue08(java.lang.String value08) {
        this.value08 = value08;
    }


    /**
     * Gets the value09 value for this MerchantClientData.
     * 
     * @return value09
     */
    public java.lang.String getValue09() {
        return value09;
    }


    /**
     * Sets the value09 value for this MerchantClientData.
     * 
     * @param value09
     */
    public void setValue09(java.lang.String value09) {
        this.value09 = value09;
    }


    /**
     * Gets the value10 value for this MerchantClientData.
     * 
     * @return value10
     */
    public java.lang.String getValue10() {
        return value10;
    }


    /**
     * Sets the value10 value for this MerchantClientData.
     * 
     * @param value10
     */
    public void setValue10(java.lang.String value10) {
        this.value10 = value10;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MerchantClientData)) return false;
        MerchantClientData other = (MerchantClientData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.value01==null && other.getValue01()==null) || 
             (this.value01!=null &&
              this.value01.equals(other.getValue01()))) &&
            ((this.value02==null && other.getValue02()==null) || 
             (this.value02!=null &&
              this.value02.equals(other.getValue02()))) &&
            ((this.value03==null && other.getValue03()==null) || 
             (this.value03!=null &&
              this.value03.equals(other.getValue03()))) &&
            ((this.value04==null && other.getValue04()==null) || 
             (this.value04!=null &&
              this.value04.equals(other.getValue04()))) &&
            ((this.value05==null && other.getValue05()==null) || 
             (this.value05!=null &&
              this.value05.equals(other.getValue05()))) &&
            ((this.value06==null && other.getValue06()==null) || 
             (this.value06!=null &&
              this.value06.equals(other.getValue06()))) &&
            ((this.value07==null && other.getValue07()==null) || 
             (this.value07!=null &&
              this.value07.equals(other.getValue07()))) &&
            ((this.value08==null && other.getValue08()==null) || 
             (this.value08!=null &&
              this.value08.equals(other.getValue08()))) &&
            ((this.value09==null && other.getValue09()==null) || 
             (this.value09!=null &&
              this.value09.equals(other.getValue09()))) &&
            ((this.value10==null && other.getValue10()==null) || 
             (this.value10!=null &&
              this.value10.equals(other.getValue10())));
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
        if (getValue01() != null) {
            _hashCode += getValue01().hashCode();
        }
        if (getValue02() != null) {
            _hashCode += getValue02().hashCode();
        }
        if (getValue03() != null) {
            _hashCode += getValue03().hashCode();
        }
        if (getValue04() != null) {
            _hashCode += getValue04().hashCode();
        }
        if (getValue05() != null) {
            _hashCode += getValue05().hashCode();
        }
        if (getValue06() != null) {
            _hashCode += getValue06().hashCode();
        }
        if (getValue07() != null) {
            _hashCode += getValue07().hashCode();
        }
        if (getValue08() != null) {
            _hashCode += getValue08().hashCode();
        }
        if (getValue09() != null) {
            _hashCode += getValue09().hashCode();
        }
        if (getValue10() != null) {
            _hashCode += getValue10().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MerchantClientData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "MerchantClientData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value01");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value01"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value02");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value02"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value03");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value03"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value04");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value04"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value05");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value05"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value06");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value06"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value07");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value07"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value08");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value08"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value09");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value09"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value10");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "value10"));
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

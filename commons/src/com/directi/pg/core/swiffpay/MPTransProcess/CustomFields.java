/**
 * CustomFields.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class CustomFields  implements java.io.Serializable {
    private java.lang.String custom1;

    private java.lang.String custom2;

    private java.lang.String custom3;

    private java.lang.String custom4;

    private java.lang.String custom5;

    private java.lang.String custom6;

    public CustomFields() {
    }

    public CustomFields(
           java.lang.String custom1,
           java.lang.String custom2,
           java.lang.String custom3,
           java.lang.String custom4,
           java.lang.String custom5,
           java.lang.String custom6) {
           this.custom1 = custom1;
           this.custom2 = custom2;
           this.custom3 = custom3;
           this.custom4 = custom4;
           this.custom5 = custom5;
           this.custom6 = custom6;
    }


    /**
     * Gets the custom1 value for this CustomFields.
     * 
     * @return custom1
     */
    public java.lang.String getCustom1() {
        return custom1;
    }


    /**
     * Sets the custom1 value for this CustomFields.
     * 
     * @param custom1
     */
    public void setCustom1(java.lang.String custom1) {
        this.custom1 = custom1;
    }


    /**
     * Gets the custom2 value for this CustomFields.
     * 
     * @return custom2
     */
    public java.lang.String getCustom2() {
        return custom2;
    }


    /**
     * Sets the custom2 value for this CustomFields.
     * 
     * @param custom2
     */
    public void setCustom2(java.lang.String custom2) {
        this.custom2 = custom2;
    }


    /**
     * Gets the custom3 value for this CustomFields.
     * 
     * @return custom3
     */
    public java.lang.String getCustom3() {
        return custom3;
    }


    /**
     * Sets the custom3 value for this CustomFields.
     * 
     * @param custom3
     */
    public void setCustom3(java.lang.String custom3) {
        this.custom3 = custom3;
    }


    /**
     * Gets the custom4 value for this CustomFields.
     * 
     * @return custom4
     */
    public java.lang.String getCustom4() {
        return custom4;
    }


    /**
     * Sets the custom4 value for this CustomFields.
     * 
     * @param custom4
     */
    public void setCustom4(java.lang.String custom4) {
        this.custom4 = custom4;
    }


    /**
     * Gets the custom5 value for this CustomFields.
     * 
     * @return custom5
     */
    public java.lang.String getCustom5() {
        return custom5;
    }


    /**
     * Sets the custom5 value for this CustomFields.
     * 
     * @param custom5
     */
    public void setCustom5(java.lang.String custom5) {
        this.custom5 = custom5;
    }


    /**
     * Gets the custom6 value for this CustomFields.
     * 
     * @return custom6
     */
    public java.lang.String getCustom6() {
        return custom6;
    }


    /**
     * Sets the custom6 value for this CustomFields.
     * 
     * @param custom6
     */
    public void setCustom6(java.lang.String custom6) {
        this.custom6 = custom6;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomFields)) return false;
        CustomFields other = (CustomFields) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.custom1==null && other.getCustom1()==null) || 
             (this.custom1!=null &&
              this.custom1.equals(other.getCustom1()))) &&
            ((this.custom2==null && other.getCustom2()==null) || 
             (this.custom2!=null &&
              this.custom2.equals(other.getCustom2()))) &&
            ((this.custom3==null && other.getCustom3()==null) || 
             (this.custom3!=null &&
              this.custom3.equals(other.getCustom3()))) &&
            ((this.custom4==null && other.getCustom4()==null) || 
             (this.custom4!=null &&
              this.custom4.equals(other.getCustom4()))) &&
            ((this.custom5==null && other.getCustom5()==null) || 
             (this.custom5!=null &&
              this.custom5.equals(other.getCustom5()))) &&
            ((this.custom6==null && other.getCustom6()==null) || 
             (this.custom6!=null &&
              this.custom6.equals(other.getCustom6())));
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
        if (getCustom1() != null) {
            _hashCode += getCustom1().hashCode();
        }
        if (getCustom2() != null) {
            _hashCode += getCustom2().hashCode();
        }
        if (getCustom3() != null) {
            _hashCode += getCustom3().hashCode();
        }
        if (getCustom4() != null) {
            _hashCode += getCustom4().hashCode();
        }
        if (getCustom5() != null) {
            _hashCode += getCustom5().hashCode();
        }
        if (getCustom6() != null) {
            _hashCode += getCustom6().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CustomFields.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom5");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom5"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom6");
        elemField.setXmlName(new javax.xml.namespace.QName("", "custom6"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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

/**
 * CustomEmail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class CustomEmail  implements java.io.Serializable {
    private java.lang.String emailto;

    private java.lang.String emailfrom;

    private java.lang.String emailsubject;

    private java.lang.String emailtext;

    public CustomEmail() {
    }

    public CustomEmail(
           java.lang.String emailto,
           java.lang.String emailfrom,
           java.lang.String emailsubject,
           java.lang.String emailtext) {
           this.emailto = emailto;
           this.emailfrom = emailfrom;
           this.emailsubject = emailsubject;
           this.emailtext = emailtext;
    }


    /**
     * Gets the emailto value for this CustomEmail.
     * 
     * @return emailto
     */
    public java.lang.String getEmailto() {
        return emailto;
    }


    /**
     * Sets the emailto value for this CustomEmail.
     * 
     * @param emailto
     */
    public void setEmailto(java.lang.String emailto) {
        this.emailto = emailto;
    }


    /**
     * Gets the emailfrom value for this CustomEmail.
     * 
     * @return emailfrom
     */
    public java.lang.String getEmailfrom() {
        return emailfrom;
    }


    /**
     * Sets the emailfrom value for this CustomEmail.
     * 
     * @param emailfrom
     */
    public void setEmailfrom(java.lang.String emailfrom) {
        this.emailfrom = emailfrom;
    }


    /**
     * Gets the emailsubject value for this CustomEmail.
     * 
     * @return emailsubject
     */
    public java.lang.String getEmailsubject() {
        return emailsubject;
    }


    /**
     * Sets the emailsubject value for this CustomEmail.
     * 
     * @param emailsubject
     */
    public void setEmailsubject(java.lang.String emailsubject) {
        this.emailsubject = emailsubject;
    }


    /**
     * Gets the emailtext value for this CustomEmail.
     * 
     * @return emailtext
     */
    public java.lang.String getEmailtext() {
        return emailtext;
    }


    /**
     * Sets the emailtext value for this CustomEmail.
     * 
     * @param emailtext
     */
    public void setEmailtext(java.lang.String emailtext) {
        this.emailtext = emailtext;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomEmail)) return false;
        CustomEmail other = (CustomEmail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.emailto==null && other.getEmailto()==null) || 
             (this.emailto!=null &&
              this.emailto.equals(other.getEmailto()))) &&
            ((this.emailfrom==null && other.getEmailfrom()==null) || 
             (this.emailfrom!=null &&
              this.emailfrom.equals(other.getEmailfrom()))) &&
            ((this.emailsubject==null && other.getEmailsubject()==null) || 
             (this.emailsubject!=null &&
              this.emailsubject.equals(other.getEmailsubject()))) &&
            ((this.emailtext==null && other.getEmailtext()==null) || 
             (this.emailtext!=null &&
              this.emailtext.equals(other.getEmailtext())));
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
        if (getEmailto() != null) {
            _hashCode += getEmailto().hashCode();
        }
        if (getEmailfrom() != null) {
            _hashCode += getEmailfrom().hashCode();
        }
        if (getEmailsubject() != null) {
            _hashCode += getEmailsubject().hashCode();
        }
        if (getEmailtext() != null) {
            _hashCode += getEmailtext().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CustomEmail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "customEmail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailfrom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailfrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailsubject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailsubject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailtext");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailtext"));
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

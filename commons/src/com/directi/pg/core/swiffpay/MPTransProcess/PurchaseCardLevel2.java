/**
 * PurchaseCardLevel2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class PurchaseCardLevel2  implements java.io.Serializable {
    private java.lang.String pocustomerrefid;

    private float taxamount;

    private int taxexempt;

    public PurchaseCardLevel2() {
    }

    public PurchaseCardLevel2(
           java.lang.String pocustomerrefid,
           float taxamount,
           int taxexempt) {
           this.pocustomerrefid = pocustomerrefid;
           this.taxamount = taxamount;
           this.taxexempt = taxexempt;
    }


    /**
     * Gets the pocustomerrefid value for this PurchaseCardLevel2.
     * 
     * @return pocustomerrefid
     */
    public java.lang.String getPocustomerrefid() {
        return pocustomerrefid;
    }


    /**
     * Sets the pocustomerrefid value for this PurchaseCardLevel2.
     * 
     * @param pocustomerrefid
     */
    public void setPocustomerrefid(java.lang.String pocustomerrefid) {
        this.pocustomerrefid = pocustomerrefid;
    }


    /**
     * Gets the taxamount value for this PurchaseCardLevel2.
     * 
     * @return taxamount
     */
    public float getTaxamount() {
        return taxamount;
    }


    /**
     * Sets the taxamount value for this PurchaseCardLevel2.
     * 
     * @param taxamount
     */
    public void setTaxamount(float taxamount) {
        this.taxamount = taxamount;
    }


    /**
     * Gets the taxexempt value for this PurchaseCardLevel2.
     * 
     * @return taxexempt
     */
    public int getTaxexempt() {
        return taxexempt;
    }


    /**
     * Sets the taxexempt value for this PurchaseCardLevel2.
     * 
     * @param taxexempt
     */
    public void setTaxexempt(int taxexempt) {
        this.taxexempt = taxexempt;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PurchaseCardLevel2)) return false;
        PurchaseCardLevel2 other = (PurchaseCardLevel2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pocustomerrefid==null && other.getPocustomerrefid()==null) || 
             (this.pocustomerrefid!=null &&
              this.pocustomerrefid.equals(other.getPocustomerrefid()))) &&
            this.taxamount == other.getTaxamount() &&
            this.taxexempt == other.getTaxexempt();
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
        if (getPocustomerrefid() != null) {
            _hashCode += getPocustomerrefid().hashCode();
        }
        _hashCode += new Float(getTaxamount()).hashCode();
        _hashCode += getTaxexempt();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PurchaseCardLevel2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "PurchaseCardLevel2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pocustomerrefid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pocustomerrefid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxamount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "taxamount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxexempt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "taxexempt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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

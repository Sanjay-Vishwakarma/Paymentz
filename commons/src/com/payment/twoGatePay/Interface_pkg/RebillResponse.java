/**
 * RebillResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

public class RebillResponse  implements java.io.Serializable {
    private java.lang.String transactionid;

    private java.lang.String transactionstatus;

    private java.lang.String transactionerrorcode;

    public RebillResponse() {
    }

    public RebillResponse(
           java.lang.String transactionid,
           java.lang.String transactionstatus,
           java.lang.String transactionerrorcode) {
           this.transactionid = transactionid;
           this.transactionstatus = transactionstatus;
           this.transactionerrorcode = transactionerrorcode;
    }


    /**
     * Gets the transactionid value for this RebillResponse.
     * 
     * @return transactionid
     */
    public java.lang.String getTransactionid() {
        return transactionid;
    }


    /**
     * Sets the transactionid value for this RebillResponse.
     * 
     * @param transactionid
     */
    public void setTransactionid(java.lang.String transactionid) {
        this.transactionid = transactionid;
    }


    /**
     * Gets the transactionstatus value for this RebillResponse.
     * 
     * @return transactionstatus
     */
    public java.lang.String getTransactionstatus() {
        return transactionstatus;
    }


    /**
     * Sets the transactionstatus value for this RebillResponse.
     * 
     * @param transactionstatus
     */
    public void setTransactionstatus(java.lang.String transactionstatus) {
        this.transactionstatus = transactionstatus;
    }


    /**
     * Gets the transactionerrorcode value for this RebillResponse.
     * 
     * @return transactionerrorcode
     */
    public java.lang.String getTransactionerrorcode() {
        return transactionerrorcode;
    }


    /**
     * Sets the transactionerrorcode value for this RebillResponse.
     * 
     * @param transactionerrorcode
     */
    public void setTransactionerrorcode(java.lang.String transactionerrorcode) {
        this.transactionerrorcode = transactionerrorcode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RebillResponse)) return false;
        RebillResponse other = (RebillResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionid==null && other.getTransactionid()==null) || 
             (this.transactionid!=null &&
              this.transactionid.equals(other.getTransactionid()))) &&
            ((this.transactionstatus==null && other.getTransactionstatus()==null) || 
             (this.transactionstatus!=null &&
              this.transactionstatus.equals(other.getTransactionstatus()))) &&
            ((this.transactionerrorcode==null && other.getTransactionerrorcode()==null) || 
             (this.transactionerrorcode!=null &&
              this.transactionerrorcode.equals(other.getTransactionerrorcode())));
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
        if (getTransactionid() != null) {
            _hashCode += getTransactionid().hashCode();
        }
        if (getTransactionstatus() != null) {
            _hashCode += getTransactionstatus().hashCode();
        }
        if (getTransactionerrorcode() != null) {
            _hashCode += getTransactionerrorcode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RebillResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "RebillResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionstatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionstatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionerrorcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionerrorcode"));
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

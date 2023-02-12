/**
 * GetTransactionStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.apco.www;

public class GetTransactionStatus  implements java.io.Serializable {
    private String merchID;

    private String merchPass;

    private String ORef;

    public GetTransactionStatus() {
    }

    public GetTransactionStatus(
           String merchID,
           String merchPass,
           String ORef) {
           this.merchID = merchID;
           this.merchPass = merchPass;
           this.ORef = ORef;
    }


    /**
     * Gets the merchID value for this GetTransactionStatus.
     * 
     * @return merchID
     */
    public String getMerchID() {
        return merchID;
    }


    /**
     * Sets the merchID value for this GetTransactionStatus.
     * 
     * @param merchID
     */
    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }


    /**
     * Gets the merchPass value for this GetTransactionStatus.
     * 
     * @return merchPass
     */
    public String getMerchPass() {
        return merchPass;
    }


    /**
     * Sets the merchPass value for this GetTransactionStatus.
     * 
     * @param merchPass
     */
    public void setMerchPass(String merchPass) {
        this.merchPass = merchPass;
    }


    /**
     * Gets the ORef value for this GetTransactionStatus.
     * 
     * @return ORef
     */
    public String getORef() {
        return ORef;
    }


    /**
     * Sets the ORef value for this GetTransactionStatus.
     * 
     * @param ORef
     */
    public void setORef(String ORef) {
        this.ORef = ORef;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetTransactionStatus)) return false;
        GetTransactionStatus other = (GetTransactionStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchID==null && other.getMerchID()==null) || 
             (this.merchID!=null &&
              this.merchID.equals(other.getMerchID()))) &&
            ((this.merchPass==null && other.getMerchPass()==null) || 
             (this.merchPass!=null &&
              this.merchPass.equals(other.getMerchPass()))) &&
            ((this.ORef==null && other.getORef()==null) || 
             (this.ORef!=null &&
              this.ORef.equals(other.getORef())));
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
        if (getMerchID() != null) {
            _hashCode += getMerchID().hashCode();
        }
        if (getMerchPass() != null) {
            _hashCode += getMerchPass().hashCode();
        }
        if (getORef() != null) {
            _hashCode += getORef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetTransactionStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://www.apsp.biz/", ">getTransactionStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://www.apsp.biz/", "MerchID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchPass");
        elemField.setXmlName(new javax.xml.namespace.QName("https://www.apsp.biz/", "MerchPass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ORef");
        elemField.setXmlName(new javax.xml.namespace.QName("https://www.apsp.biz/", "ORef"));
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

/**
 * CreditCardBatchFile.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class CreditCardBatchFile  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity;

    private java.lang.String fileID;

    private byte[] batchFile;

    public CreditCardBatchFile() {
    }

    public CreditCardBatchFile(
            com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity,
           java.lang.String fileID,
           byte[] batchFile) {
           this.serviceSecurity = serviceSecurity;
           this.fileID = fileID;
           this.batchFile = batchFile;
    }


    /**
     * Gets the serviceSecurity value for this CreditCardBatchFile.
     * 
     * @return serviceSecurity
     */
    public com.payment.Oculus.com.MyCardStorage.ServiceSecurity getServiceSecurity() {
        return serviceSecurity;
    }


    /**
     * Sets the serviceSecurity value for this CreditCardBatchFile.
     * 
     * @param serviceSecurity
     */
    public void setServiceSecurity(com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity) {
        this.serviceSecurity = serviceSecurity;
    }


    /**
     * Gets the fileID value for this CreditCardBatchFile.
     * 
     * @return fileID
     */
    public java.lang.String getFileID() {
        return fileID;
    }


    /**
     * Sets the fileID value for this CreditCardBatchFile.
     * 
     * @param fileID
     */
    public void setFileID(java.lang.String fileID) {
        this.fileID = fileID;
    }


    /**
     * Gets the batchFile value for this CreditCardBatchFile.
     * 
     * @return batchFile
     */
    public byte[] getBatchFile() {
        return batchFile;
    }


    /**
     * Sets the batchFile value for this CreditCardBatchFile.
     * 
     * @param batchFile
     */
    public void setBatchFile(byte[] batchFile) {
        this.batchFile = batchFile;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreditCardBatchFile)) return false;
        CreditCardBatchFile other = (CreditCardBatchFile) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceSecurity==null && other.getServiceSecurity()==null) || 
             (this.serviceSecurity!=null &&
              this.serviceSecurity.equals(other.getServiceSecurity()))) &&
            ((this.fileID==null && other.getFileID()==null) || 
             (this.fileID!=null &&
              this.fileID.equals(other.getFileID()))) &&
            ((this.batchFile==null && other.getBatchFile()==null) || 
             (this.batchFile!=null &&
              java.util.Arrays.equals(this.batchFile, other.getBatchFile())));
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
        if (getServiceSecurity() != null) {
            _hashCode += getServiceSecurity().hashCode();
        }
        if (getFileID() != null) {
            _hashCode += getFileID().hashCode();
        }
        if (getBatchFile() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBatchFile());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBatchFile(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreditCardBatchFile.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CreditCardBatchFile"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceSecurity");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "FileID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchFile");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BatchFile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
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

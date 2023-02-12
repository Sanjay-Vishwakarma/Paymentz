/**
 * S3DDataSubmitResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public class S3DDataSubmitResponse  implements java.io.Serializable {
    private java.lang.String merchantorderid;

    private int transactionid;

    private int s3D;

    private java.lang.String status;

    private java.lang.String errorcode;

    public S3DDataSubmitResponse() {
    }

    public S3DDataSubmitResponse(
           java.lang.String merchantorderid,
           int transactionid,
           int s3D,
           java.lang.String status,
           java.lang.String errorcode) {
           this.merchantorderid = merchantorderid;
           this.transactionid = transactionid;
           this.s3D = s3D;
           this.status = status;
           this.errorcode = errorcode;
    }


    /**
     * Gets the merchantorderid value for this S3DDataSubmitResponse.
     * 
     * @return merchantorderid
     */
    public java.lang.String getMerchantorderid() {
        return merchantorderid;
    }


    /**
     * Sets the merchantorderid value for this S3DDataSubmitResponse.
     * 
     * @param merchantorderid
     */
    public void setMerchantorderid(java.lang.String merchantorderid) {
        this.merchantorderid = merchantorderid;
    }


    /**
     * Gets the transactionid value for this S3DDataSubmitResponse.
     * 
     * @return transactionid
     */
    public int getTransactionid() {
        return transactionid;
    }


    /**
     * Sets the transactionid value for this S3DDataSubmitResponse.
     * 
     * @param transactionid
     */
    public void setTransactionid(int transactionid) {
        this.transactionid = transactionid;
    }


    /**
     * Gets the s3D value for this S3DDataSubmitResponse.
     * 
     * @return s3D
     */
    public int getS3D() {
        return s3D;
    }


    /**
     * Sets the s3D value for this S3DDataSubmitResponse.
     * 
     * @param s3D
     */
    public void setS3D(int s3D) {
        this.s3D = s3D;
    }


    /**
     * Gets the status value for this S3DDataSubmitResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this S3DDataSubmitResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the errorcode value for this S3DDataSubmitResponse.
     * 
     * @return errorcode
     */
    public java.lang.String getErrorcode() {
        return errorcode;
    }


    /**
     * Sets the errorcode value for this S3DDataSubmitResponse.
     * 
     * @param errorcode
     */
    public void setErrorcode(java.lang.String errorcode) {
        this.errorcode = errorcode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof S3DDataSubmitResponse)) return false;
        S3DDataSubmitResponse other = (S3DDataSubmitResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchantorderid==null && other.getMerchantorderid()==null) || 
             (this.merchantorderid!=null &&
              this.merchantorderid.equals(other.getMerchantorderid()))) &&
            this.transactionid == other.getTransactionid() &&
            this.s3D == other.getS3D() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.errorcode==null && other.getErrorcode()==null) || 
             (this.errorcode!=null &&
              this.errorcode.equals(other.getErrorcode())));
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
        if (getMerchantorderid() != null) {
            _hashCode += getMerchantorderid().hashCode();
        }
        _hashCode += getTransactionid();
        _hashCode += getS3D();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getErrorcode() != null) {
            _hashCode += getErrorcode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(S3DDataSubmitResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "S3DDataSubmitResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantorderid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantorderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("s3D");
        elemField.setXmlName(new javax.xml.namespace.QName("", "s3d"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorcode"));
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

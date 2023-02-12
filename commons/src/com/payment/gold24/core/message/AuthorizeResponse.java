/**
 * AuthorizeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public class AuthorizeResponse  implements java.io.Serializable {
    private java.lang.String merchantorderid;

    private int transactionid;

    private java.lang.String status;

    private java.lang.String errorcode;

    private java.lang.String acsurl;

    private java.lang.String md;

    private java.lang.String acsrequestmessage;

    private java.lang.String descriptor;

    public AuthorizeResponse() {
    }

    public AuthorizeResponse(
           java.lang.String merchantorderid,
           int transactionid,
           java.lang.String status,
           java.lang.String errorcode,
           java.lang.String acsurl,
           java.lang.String md,
           java.lang.String acsrequestmessage,
           java.lang.String descriptor) {
           this.merchantorderid = merchantorderid;
           this.transactionid = transactionid;
           this.status = status;
           this.errorcode = errorcode;
           this.acsurl = acsurl;
           this.md = md;
           this.acsrequestmessage = acsrequestmessage;
           this.descriptor = descriptor;
    }


    /**
     * Gets the merchantorderid value for this AuthorizeResponse.
     * 
     * @return merchantorderid
     */
    public java.lang.String getMerchantorderid() {
        return merchantorderid;
    }


    /**
     * Sets the merchantorderid value for this AuthorizeResponse.
     * 
     * @param merchantorderid
     */
    public void setMerchantorderid(java.lang.String merchantorderid) {
        this.merchantorderid = merchantorderid;
    }


    /**
     * Gets the transactionid value for this AuthorizeResponse.
     * 
     * @return transactionid
     */
    public int getTransactionid() {
        return transactionid;
    }


    /**
     * Sets the transactionid value for this AuthorizeResponse.
     * 
     * @param transactionid
     */
    public void setTransactionid(int transactionid) {
        this.transactionid = transactionid;
    }


    /**
     * Gets the status value for this AuthorizeResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AuthorizeResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the errorcode value for this AuthorizeResponse.
     * 
     * @return errorcode
     */
    public java.lang.String getErrorcode() {
        return errorcode;
    }


    /**
     * Sets the errorcode value for this AuthorizeResponse.
     * 
     * @param errorcode
     */
    public void setErrorcode(java.lang.String errorcode) {
        this.errorcode = errorcode;
    }


    /**
     * Gets the acsurl value for this AuthorizeResponse.
     * 
     * @return acsurl
     */
    public java.lang.String getAcsurl() {
        return acsurl;
    }


    /**
     * Sets the acsurl value for this AuthorizeResponse.
     * 
     * @param acsurl
     */
    public void setAcsurl(java.lang.String acsurl) {
        this.acsurl = acsurl;
    }


    /**
     * Gets the md value for this AuthorizeResponse.
     * 
     * @return md
     */
    public java.lang.String getMd() {
        return md;
    }


    /**
     * Sets the md value for this AuthorizeResponse.
     * 
     * @param md
     */
    public void setMd(java.lang.String md) {
        this.md = md;
    }


    /**
     * Gets the acsrequestmessage value for this AuthorizeResponse.
     * 
     * @return acsrequestmessage
     */
    public java.lang.String getAcsrequestmessage() {
        return acsrequestmessage;
    }


    /**
     * Sets the acsrequestmessage value for this AuthorizeResponse.
     * 
     * @param acsrequestmessage
     */
    public void setAcsrequestmessage(java.lang.String acsrequestmessage) {
        this.acsrequestmessage = acsrequestmessage;
    }


    /**
     * Gets the descriptor value for this AuthorizeResponse.
     * 
     * @return descriptor
     */
    public java.lang.String getDescriptor() {
        return descriptor;
    }


    /**
     * Sets the descriptor value for this AuthorizeResponse.
     * 
     * @param descriptor
     */
    public void setDescriptor(java.lang.String descriptor) {
        this.descriptor = descriptor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorizeResponse)) return false;
        AuthorizeResponse other = (AuthorizeResponse) obj;
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
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.errorcode==null && other.getErrorcode()==null) || 
             (this.errorcode!=null &&
              this.errorcode.equals(other.getErrorcode()))) &&
            ((this.acsurl==null && other.getAcsurl()==null) || 
             (this.acsurl!=null &&
              this.acsurl.equals(other.getAcsurl()))) &&
            ((this.md==null && other.getMd()==null) || 
             (this.md!=null &&
              this.md.equals(other.getMd()))) &&
            ((this.acsrequestmessage==null && other.getAcsrequestmessage()==null) || 
             (this.acsrequestmessage!=null &&
              this.acsrequestmessage.equals(other.getAcsrequestmessage()))) &&
            ((this.descriptor==null && other.getDescriptor()==null) || 
             (this.descriptor!=null &&
              this.descriptor.equals(other.getDescriptor())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getErrorcode() != null) {
            _hashCode += getErrorcode().hashCode();
        }
        if (getAcsurl() != null) {
            _hashCode += getAcsurl().hashCode();
        }
        if (getMd() != null) {
            _hashCode += getMd().hashCode();
        }
        if (getAcsrequestmessage() != null) {
            _hashCode += getAcsrequestmessage().hashCode();
        }
        if (getDescriptor() != null) {
            _hashCode += getDescriptor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorizeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "AuthorizeResponse"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsurl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acsurl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("md");
        elemField.setXmlName(new javax.xml.namespace.QName("", "md"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsrequestmessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acsrequestmessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "descriptor"));
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

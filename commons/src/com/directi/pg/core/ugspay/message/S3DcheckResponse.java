/**
 * S3DcheckResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public class S3DcheckResponse  implements java.io.Serializable {
    private int transactionID;

    private java.lang.String status;

    private java.lang.String errorcode;

    private java.lang.String errormessage;

    private java.lang.String ACSUrl;

    private java.lang.String ACSRequestMessage;

    public S3DcheckResponse() {
    }

    public S3DcheckResponse(
           int transactionID,
           java.lang.String status,
           java.lang.String errorcode,
           java.lang.String errormessage,
           java.lang.String ACSUrl,
           java.lang.String ACSRequestMessage) {
           this.transactionID = transactionID;
           this.status = status;
           this.errorcode = errorcode;
           this.errormessage = errormessage;
           this.ACSUrl = ACSUrl;
           this.ACSRequestMessage = ACSRequestMessage;
    }


    /**
     * Gets the transactionID value for this S3DcheckResponse.
     * 
     * @return transactionID
     */
    public int getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this S3DcheckResponse.
     * 
     * @param transactionID
     */
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the status value for this S3DcheckResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this S3DcheckResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the errorcode value for this S3DcheckResponse.
     * 
     * @return errorcode
     */
    public java.lang.String getErrorcode() {
        return errorcode;
    }


    /**
     * Sets the errorcode value for this S3DcheckResponse.
     * 
     * @param errorcode
     */
    public void setErrorcode(java.lang.String errorcode) {
        this.errorcode = errorcode;
    }


    /**
     * Gets the errormessage value for this S3DcheckResponse.
     * 
     * @return errormessage
     */
    public java.lang.String getErrormessage() {
        return errormessage;
    }


    /**
     * Sets the errormessage value for this S3DcheckResponse.
     * 
     * @param errormessage
     */
    public void setErrormessage(java.lang.String errormessage) {
        this.errormessage = errormessage;
    }


    /**
     * Gets the ACSUrl value for this S3DcheckResponse.
     * 
     * @return ACSUrl
     */
    public java.lang.String getACSUrl() {
        return ACSUrl;
    }


    /**
     * Sets the ACSUrl value for this S3DcheckResponse.
     * 
     * @param ACSUrl
     */
    public void setACSUrl(java.lang.String ACSUrl) {
        this.ACSUrl = ACSUrl;
    }


    /**
     * Gets the ACSRequestMessage value for this S3DcheckResponse.
     * 
     * @return ACSRequestMessage
     */
    public java.lang.String getACSRequestMessage() {
        return ACSRequestMessage;
    }


    /**
     * Sets the ACSRequestMessage value for this S3DcheckResponse.
     * 
     * @param ACSRequestMessage
     */
    public void setACSRequestMessage(java.lang.String ACSRequestMessage) {
        this.ACSRequestMessage = ACSRequestMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof S3DcheckResponse)) return false;
        S3DcheckResponse other = (S3DcheckResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.transactionID == other.getTransactionID() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.errorcode==null && other.getErrorcode()==null) || 
             (this.errorcode!=null &&
              this.errorcode.equals(other.getErrorcode()))) &&
            ((this.errormessage==null && other.getErrormessage()==null) || 
             (this.errormessage!=null &&
              this.errormessage.equals(other.getErrormessage()))) &&
            ((this.ACSUrl==null && other.getACSUrl()==null) || 
             (this.ACSUrl!=null &&
              this.ACSUrl.equals(other.getACSUrl()))) &&
            ((this.ACSRequestMessage==null && other.getACSRequestMessage()==null) || 
             (this.ACSRequestMessage!=null &&
              this.ACSRequestMessage.equals(other.getACSRequestMessage())));
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
        _hashCode += getTransactionID();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getErrorcode() != null) {
            _hashCode += getErrorcode().hashCode();
        }
        if (getErrormessage() != null) {
            _hashCode += getErrormessage().hashCode();
        }
        if (getACSUrl() != null) {
            _hashCode += getACSUrl().hashCode();
        }
        if (getACSRequestMessage() != null) {
            _hashCode += getACSRequestMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(S3DcheckResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "S3dcheckResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionID"));
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
        elemField.setFieldName("errormessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errormessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ACSUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ACSUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ACSRequestMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ACSRequestMessage"));
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

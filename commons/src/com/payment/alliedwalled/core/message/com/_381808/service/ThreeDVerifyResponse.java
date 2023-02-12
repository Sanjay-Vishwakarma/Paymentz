/**
 * ThreeDVerifyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.alliedwalled.core.message.com._381808.service;

public class ThreeDVerifyResponse  implements java.io.Serializable {
    private int state;

    private int status;

    private java.lang.String message;

    private java.lang.String technical;

    private java.lang.String transactionID;

    private java.lang.String MD;

    private java.lang.String paReq;

    private java.lang.String acsURL;

    public ThreeDVerifyResponse() {
    }

    public ThreeDVerifyResponse(
           int state,
           int status,
           java.lang.String message,
           java.lang.String technical,
           java.lang.String transactionID,
           java.lang.String MD,
           java.lang.String paReq,
           java.lang.String acsURL) {
           this.state = state;
           this.status = status;
           this.message = message;
           this.technical = technical;
           this.transactionID = transactionID;
           this.MD = MD;
           this.paReq = paReq;
           this.acsURL = acsURL;
    }


    /**
     * Gets the state value for this ThreeDVerifyResponse.
     * 
     * @return state
     */
    public int getState() {
        return state;
    }


    /**
     * Sets the state value for this ThreeDVerifyResponse.
     * 
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }


    /**
     * Gets the status value for this ThreeDVerifyResponse.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ThreeDVerifyResponse.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }


    /**
     * Gets the message value for this ThreeDVerifyResponse.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this ThreeDVerifyResponse.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the technical value for this ThreeDVerifyResponse.
     * 
     * @return technical
     */
    public java.lang.String getTechnical() {
        return technical;
    }


    /**
     * Sets the technical value for this ThreeDVerifyResponse.
     * 
     * @param technical
     */
    public void setTechnical(java.lang.String technical) {
        this.technical = technical;
    }


    /**
     * Gets the transactionID value for this ThreeDVerifyResponse.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this ThreeDVerifyResponse.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the MD value for this ThreeDVerifyResponse.
     * 
     * @return MD
     */
    public java.lang.String getMD() {
        return MD;
    }


    /**
     * Sets the MD value for this ThreeDVerifyResponse.
     * 
     * @param MD
     */
    public void setMD(java.lang.String MD) {
        this.MD = MD;
    }


    /**
     * Gets the paReq value for this ThreeDVerifyResponse.
     * 
     * @return paReq
     */
    public java.lang.String getPaReq() {
        return paReq;
    }


    /**
     * Sets the paReq value for this ThreeDVerifyResponse.
     * 
     * @param paReq
     */
    public void setPaReq(java.lang.String paReq) {
        this.paReq = paReq;
    }


    /**
     * Gets the acsURL value for this ThreeDVerifyResponse.
     * 
     * @return acsURL
     */
    public java.lang.String getAcsURL() {
        return acsURL;
    }


    /**
     * Sets the acsURL value for this ThreeDVerifyResponse.
     * 
     * @param acsURL
     */
    public void setAcsURL(java.lang.String acsURL) {
        this.acsURL = acsURL;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ThreeDVerifyResponse)) return false;
        ThreeDVerifyResponse other = (ThreeDVerifyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.state == other.getState() &&
            this.status == other.getStatus() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.technical==null && other.getTechnical()==null) || 
             (this.technical!=null &&
              this.technical.equals(other.getTechnical()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.MD==null && other.getMD()==null) || 
             (this.MD!=null &&
              this.MD.equals(other.getMD()))) &&
            ((this.paReq==null && other.getPaReq()==null) || 
             (this.paReq!=null &&
              this.paReq.equals(other.getPaReq()))) &&
            ((this.acsURL==null && other.getAcsURL()==null) || 
             (this.acsURL!=null &&
              this.acsURL.equals(other.getAcsURL())));
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
        _hashCode += getState();
        _hashCode += getStatus();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getTechnical() != null) {
            _hashCode += getTechnical().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getMD() != null) {
            _hashCode += getMD().hashCode();
        }
        if (getPaReq() != null) {
            _hashCode += getPaReq().hashCode();
        }
        if (getAcsURL() != null) {
            _hashCode += getAcsURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ThreeDVerifyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://service.381808.com/", "ThreeDVerifyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "State"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("technical");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "Technical"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "TransactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MD");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "MD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "PaReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://service.381808.com/", "AcsURL"));
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

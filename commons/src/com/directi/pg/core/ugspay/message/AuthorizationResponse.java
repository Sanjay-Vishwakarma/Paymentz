/**
 * AuthorizationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public class AuthorizationResponse  implements java.io.Serializable {
    private int transactionID;

    private java.lang.String orderID;

    private java.lang.String status;

    private java.lang.String errorcode;

    private java.lang.String errormessage;

    private java.lang.String AVSResult;

    private java.lang.String FSResult;

    private java.lang.String FSStatus;

    private java.lang.String statementDescriptor;

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(
           int transactionID,
           java.lang.String orderID,
           java.lang.String status,
           java.lang.String errorcode,
           java.lang.String errormessage,
           java.lang.String AVSResult,
           java.lang.String FSResult,
           java.lang.String FSStatus,
           java.lang.String statementDescriptor) {
           this.transactionID = transactionID;
           this.orderID = orderID;
           this.status = status;
           this.errorcode = errorcode;
           this.errormessage = errormessage;
           this.AVSResult = AVSResult;
           this.FSResult = FSResult;
           this.FSStatus = FSStatus;
           this.statementDescriptor = statementDescriptor;
    }


    /**
     * Gets the transactionID value for this AuthorizationResponse.
     * 
     * @return transactionID
     */
    public int getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this AuthorizationResponse.
     * 
     * @param transactionID
     */
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the orderID value for this AuthorizationResponse.
     * 
     * @return orderID
     */
    public java.lang.String getOrderID() {
        return orderID;
    }


    /**
     * Sets the orderID value for this AuthorizationResponse.
     * 
     * @param orderID
     */
    public void setOrderID(java.lang.String orderID) {
        this.orderID = orderID;
    }


    /**
     * Gets the status value for this AuthorizationResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AuthorizationResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the errorcode value for this AuthorizationResponse.
     * 
     * @return errorcode
     */
    public java.lang.String getErrorcode() {
        return errorcode;
    }


    /**
     * Sets the errorcode value for this AuthorizationResponse.
     * 
     * @param errorcode
     */
    public void setErrorcode(java.lang.String errorcode) {
        this.errorcode = errorcode;
    }


    /**
     * Gets the errormessage value for this AuthorizationResponse.
     * 
     * @return errormessage
     */
    public java.lang.String getErrormessage() {
        return errormessage;
    }


    /**
     * Sets the errormessage value for this AuthorizationResponse.
     * 
     * @param errormessage
     */
    public void setErrormessage(java.lang.String errormessage) {
        this.errormessage = errormessage;
    }


    /**
     * Gets the AVSResult value for this AuthorizationResponse.
     * 
     * @return AVSResult
     */
    public java.lang.String getAVSResult() {
        return AVSResult;
    }


    /**
     * Sets the AVSResult value for this AuthorizationResponse.
     * 
     * @param AVSResult
     */
    public void setAVSResult(java.lang.String AVSResult) {
        this.AVSResult = AVSResult;
    }


    /**
     * Gets the FSResult value for this AuthorizationResponse.
     * 
     * @return FSResult
     */
    public java.lang.String getFSResult() {
        return FSResult;
    }


    /**
     * Sets the FSResult value for this AuthorizationResponse.
     * 
     * @param FSResult
     */
    public void setFSResult(java.lang.String FSResult) {
        this.FSResult = FSResult;
    }


    /**
     * Gets the FSStatus value for this AuthorizationResponse.
     * 
     * @return FSStatus
     */
    public java.lang.String getFSStatus() {
        return FSStatus;
    }


    /**
     * Sets the FSStatus value for this AuthorizationResponse.
     * 
     * @param FSStatus
     */
    public void setFSStatus(java.lang.String FSStatus) {
        this.FSStatus = FSStatus;
    }


    /**
     * Gets the statementDescriptor value for this AuthorizationResponse.
     * 
     * @return statementDescriptor
     */
    public java.lang.String getStatementDescriptor() {
        return statementDescriptor;
    }


    /**
     * Sets the statementDescriptor value for this AuthorizationResponse.
     * 
     * @param statementDescriptor
     */
    public void setStatementDescriptor(java.lang.String statementDescriptor) {
        this.statementDescriptor = statementDescriptor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorizationResponse)) return false;
        AuthorizationResponse other = (AuthorizationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.transactionID == other.getTransactionID() &&
            ((this.orderID==null && other.getOrderID()==null) || 
             (this.orderID!=null &&
              this.orderID.equals(other.getOrderID()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.errorcode==null && other.getErrorcode()==null) || 
             (this.errorcode!=null &&
              this.errorcode.equals(other.getErrorcode()))) &&
            ((this.errormessage==null && other.getErrormessage()==null) || 
             (this.errormessage!=null &&
              this.errormessage.equals(other.getErrormessage()))) &&
            ((this.AVSResult==null && other.getAVSResult()==null) || 
             (this.AVSResult!=null &&
              this.AVSResult.equals(other.getAVSResult()))) &&
            ((this.FSResult==null && other.getFSResult()==null) || 
             (this.FSResult!=null &&
              this.FSResult.equals(other.getFSResult()))) &&
            ((this.FSStatus==null && other.getFSStatus()==null) || 
             (this.FSStatus!=null &&
              this.FSStatus.equals(other.getFSStatus()))) &&
            ((this.statementDescriptor==null && other.getStatementDescriptor()==null) || 
             (this.statementDescriptor!=null &&
              this.statementDescriptor.equals(other.getStatementDescriptor())));
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
        if (getOrderID() != null) {
            _hashCode += getOrderID().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getErrorcode() != null) {
            _hashCode += getErrorcode().hashCode();
        }
        if (getErrormessage() != null) {
            _hashCode += getErrormessage().hashCode();
        }
        if (getAVSResult() != null) {
            _hashCode += getAVSResult().hashCode();
        }
        if (getFSResult() != null) {
            _hashCode += getFSResult().hashCode();
        }
        if (getFSStatus() != null) {
            _hashCode += getFSStatus().hashCode();
        }
        if (getStatementDescriptor() != null) {
            _hashCode += getStatementDescriptor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorizationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "AuthorizationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("AVSResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AVSResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statementDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statementDescriptor"));
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

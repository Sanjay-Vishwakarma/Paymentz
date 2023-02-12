/**
 * S3DDataSubmitResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

public class S3DDataSubmitResponse  implements java.io.Serializable {
    private String transactionID;

    private String orderID;

    private String status;

    private String errorCode;

    private String errorMessage;

    private String errorDescription;

    private String AVSResult;

    private String FSResult;

    private String FSStatus;

    private String statementDescriptor;

    private String ACSUrl;

    private String ACSRequestMessage;

    public S3DDataSubmitResponse() {
    }

    public S3DDataSubmitResponse(
           String transactionID,
           String orderID,
           String status,
           String errorCode,
           String errorMessage,
           String errorDescription,
           String AVSResult,
           String FSResult,
           String FSStatus,
           String statementDescriptor,
           String ACSUrl,
           String ACSRequestMessage) {
           this.transactionID = transactionID;
           this.orderID = orderID;
           this.status = status;
           this.errorCode = errorCode;
           this.errorMessage = errorMessage;
           this.errorDescription = errorDescription;
           this.AVSResult = AVSResult;
           this.FSResult = FSResult;
           this.FSStatus = FSStatus;
           this.statementDescriptor = statementDescriptor;
           this.ACSUrl = ACSUrl;
           this.ACSRequestMessage = ACSRequestMessage;
    }


    /**
     * Gets the transactionID value for this S3DDataSubmitResponse.
     * 
     * @return transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this S3DDataSubmitResponse.
     * 
     * @param transactionID
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the orderID value for this S3DDataSubmitResponse.
     * 
     * @return orderID
     */
    public String getOrderID() {
        return orderID;
    }


    /**
     * Sets the orderID value for this S3DDataSubmitResponse.
     * 
     * @param orderID
     */
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }


    /**
     * Gets the status value for this S3DDataSubmitResponse.
     * 
     * @return status
     */
    public String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this S3DDataSubmitResponse.
     * 
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Gets the errorCode value for this S3DDataSubmitResponse.
     * 
     * @return errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this S3DDataSubmitResponse.
     * 
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorMessage value for this S3DDataSubmitResponse.
     * 
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this S3DDataSubmitResponse.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the errorDescription value for this S3DDataSubmitResponse.
     * 
     * @return errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }


    /**
     * Sets the errorDescription value for this S3DDataSubmitResponse.
     * 
     * @param errorDescription
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }


    /**
     * Gets the AVSResult value for this S3DDataSubmitResponse.
     * 
     * @return AVSResult
     */
    public String getAVSResult() {
        return AVSResult;
    }


    /**
     * Sets the AVSResult value for this S3DDataSubmitResponse.
     * 
     * @param AVSResult
     */
    public void setAVSResult(String AVSResult) {
        this.AVSResult = AVSResult;
    }


    /**
     * Gets the FSResult value for this S3DDataSubmitResponse.
     * 
     * @return FSResult
     */
    public String getFSResult() {
        return FSResult;
    }


    /**
     * Sets the FSResult value for this S3DDataSubmitResponse.
     * 
     * @param FSResult
     */
    public void setFSResult(String FSResult) {
        this.FSResult = FSResult;
    }


    /**
     * Gets the FSStatus value for this S3DDataSubmitResponse.
     * 
     * @return FSStatus
     */
    public String getFSStatus() {
        return FSStatus;
    }


    /**
     * Sets the FSStatus value for this S3DDataSubmitResponse.
     * 
     * @param FSStatus
     */
    public void setFSStatus(String FSStatus) {
        this.FSStatus = FSStatus;
    }


    /**
     * Gets the statementDescriptor value for this S3DDataSubmitResponse.
     * 
     * @return statementDescriptor
     */
    public String getStatementDescriptor() {
        return statementDescriptor;
    }


    /**
     * Sets the statementDescriptor value for this S3DDataSubmitResponse.
     * 
     * @param statementDescriptor
     */
    public void setStatementDescriptor(String statementDescriptor) {
        this.statementDescriptor = statementDescriptor;
    }


    /**
     * Gets the ACSUrl value for this S3DDataSubmitResponse.
     * 
     * @return ACSUrl
     */
    public String getACSUrl() {
        return ACSUrl;
    }


    /**
     * Sets the ACSUrl value for this S3DDataSubmitResponse.
     * 
     * @param ACSUrl
     */
    public void setACSUrl(String ACSUrl) {
        this.ACSUrl = ACSUrl;
    }


    /**
     * Gets the ACSRequestMessage value for this S3DDataSubmitResponse.
     * 
     * @return ACSRequestMessage
     */
    public String getACSRequestMessage() {
        return ACSRequestMessage;
    }


    /**
     * Sets the ACSRequestMessage value for this S3DDataSubmitResponse.
     * 
     * @param ACSRequestMessage
     */
    public void setACSRequestMessage(String ACSRequestMessage) {
        this.ACSRequestMessage = ACSRequestMessage;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
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
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.orderID==null && other.getOrderID()==null) || 
             (this.orderID!=null &&
              this.orderID.equals(other.getOrderID()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            ((this.errorDescription==null && other.getErrorDescription()==null) || 
             (this.errorDescription!=null &&
              this.errorDescription.equals(other.getErrorDescription()))) &&
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
              this.statementDescriptor.equals(other.getStatementDescriptor()))) &&
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
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getOrderID() != null) {
            _hashCode += getOrderID().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        if (getErrorDescription() != null) {
            _hashCode += getErrorDescription().hashCode();
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
        new org.apache.axis.description.TypeDesc(S3DDataSubmitResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "S3DDataSubmitResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ErrorDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AVSResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AVSResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statementDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statementDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ACSUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ACSUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ACSRequestMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ACSRequestMessage"));
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

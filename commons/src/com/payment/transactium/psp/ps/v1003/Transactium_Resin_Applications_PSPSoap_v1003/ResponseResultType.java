/**
 * ResponseResultType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseResultType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseResultType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.ResultType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ResultCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MAP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processingTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ProcessingTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "duration"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "TimeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode code;
    private String hostCode;
    private Boolean MAP;
    private String message;
    private org.apache.axis.types.Duration processingTime;
    private java.util.Calendar timeStamp;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseResultType() {
    }


    public ResponseResultType(
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode code,
           String hostCode,
           Boolean MAP,
           String message,
           org.apache.axis.types.Duration processingTime,
           java.util.Calendar timeStamp) {
           this.code = code;
           this.hostCode = hostCode;
           this.MAP = MAP;
           this.message = message;
           this.processingTime = processingTime;
           this.timeStamp = timeStamp;
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

    /**
     * Gets the code value for this ResponseResultType.
     *
     * @return code
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode getCode() {
        return code;
    }

    /**
     * Sets the code value for this ResponseResultType.
     *
     * @param code
     */
    public void setCode(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode code) {
        this.code = code;
    }

    /**
     * Gets the hostCode value for this ResponseResultType.
     *
     * @return hostCode
     */
    public String getHostCode() {
        return hostCode;
    }

    /**
     * Sets the hostCode value for this ResponseResultType.
     *
     * @param hostCode
     */
    public void setHostCode(String hostCode) {
        this.hostCode = hostCode;
    }

    /**
     * Gets the MAP value for this ResponseResultType.
     *
     * @return MAP
     */
    public Boolean getMAP() {
        return MAP;
    }

    /**
     * Sets the MAP value for this ResponseResultType.
     *
     * @param MAP
     */
    public void setMAP(Boolean MAP) {
        this.MAP = MAP;
    }

    /**
     * Gets the message value for this ResponseResultType.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this ResponseResultType.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the processingTime value for this ResponseResultType.
     *
     * @return processingTime
     */
    public org.apache.axis.types.Duration getProcessingTime() {
        return processingTime;
    }

    /**
     * Sets the processingTime value for this ResponseResultType.
     *
     * @param processingTime
     */
    public void setProcessingTime(org.apache.axis.types.Duration processingTime) {
        this.processingTime = processingTime;
    }

    /**
     * Gets the timeStamp value for this ResponseResultType.
     *
     * @return timeStamp
     */
    public java.util.Calendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the timeStamp value for this ResponseResultType.
     *
     * @param timeStamp
     */
    public void setTimeStamp(java.util.Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseResultType)) return false;
        ResponseResultType other = (ResponseResultType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.code==null && other.getCode()==null) ||
             (this.code!=null &&
              this.code.equals(other.getCode()))) &&
            ((this.hostCode==null && other.getHostCode()==null) ||
             (this.hostCode!=null &&
              this.hostCode.equals(other.getHostCode()))) &&
            ((this.MAP==null && other.getMAP()==null) ||
             (this.MAP!=null &&
              this.MAP.equals(other.getMAP()))) &&
            ((this.message==null && other.getMessage()==null) ||
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.processingTime==null && other.getProcessingTime()==null) ||
             (this.processingTime!=null &&
              this.processingTime.equals(other.getProcessingTime()))) &&
            ((this.timeStamp==null && other.getTimeStamp()==null) ||
             (this.timeStamp!=null &&
              this.timeStamp.equals(other.getTimeStamp())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getHostCode() != null) {
            _hashCode += getHostCode().hashCode();
        }
        if (getMAP() != null) {
            _hashCode += getMAP().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getProcessingTime() != null) {
            _hashCode += getProcessingTime().hashCode();
        }
        if (getTimeStamp() != null) {
            _hashCode += getTimeStamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

/**
 * CloseBatchResponseCloseBatchDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class CloseBatchResponseCloseBatchDetail  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CloseBatchResponseCloseBatchDetail.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CloseBatchResponse.CloseBatchDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BatchId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BatchNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ResultCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Terminal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String account;
    private org.apache.axis.types.UnsignedInt batchId;
    private String batchNumber;
    private String message;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result;
    private String terminal;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public CloseBatchResponseCloseBatchDetail() {
    }


    public CloseBatchResponseCloseBatchDetail(
           String account,
           org.apache.axis.types.UnsignedInt batchId,
           String batchNumber,
           String message,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result,
           String terminal) {
           this.account = account;
           this.batchId = batchId;
           this.batchNumber = batchNumber;
           this.message = message;
           this.result = result;
           this.terminal = terminal;
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
     * Gets the account value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Gets the batchId value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return batchId
     */
    public org.apache.axis.types.UnsignedInt getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param batchId
     */
    public void setBatchId(org.apache.axis.types.UnsignedInt batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the batchNumber value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return batchNumber
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * Sets the batchNumber value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param batchNumber
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * Gets the message value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the result value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return result
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode getResult() {
        return result;
    }

    /**
     * Sets the result value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param result
     */
    public void setResult(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result) {
        this.result = result;
    }

    /**
     * Gets the terminal value for this CloseBatchResponseCloseBatchDetail.
     *
     * @return terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal value for this CloseBatchResponseCloseBatchDetail.
     *
     * @param terminal
     */
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CloseBatchResponseCloseBatchDetail)) return false;
        CloseBatchResponseCloseBatchDetail other = (CloseBatchResponseCloseBatchDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.account==null && other.getAccount()==null) ||
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            ((this.batchId==null && other.getBatchId()==null) ||
             (this.batchId!=null &&
              this.batchId.equals(other.getBatchId()))) &&
            ((this.batchNumber==null && other.getBatchNumber()==null) ||
             (this.batchNumber!=null &&
              this.batchNumber.equals(other.getBatchNumber()))) &&
            ((this.message==null && other.getMessage()==null) ||
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.result==null && other.getResult()==null) ||
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.terminal==null && other.getTerminal()==null) ||
             (this.terminal!=null &&
              this.terminal.equals(other.getTerminal())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getBatchId() != null) {
            _hashCode += getBatchId().hashCode();
        }
        if (getBatchNumber() != null) {
            _hashCode += getBatchNumber().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getTerminal() != null) {
            _hashCode += getTerminal().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

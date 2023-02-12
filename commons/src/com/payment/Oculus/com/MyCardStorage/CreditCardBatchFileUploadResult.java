/**
 * CreditCardBatchFileUploadResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class CreditCardBatchFileUploadResult  implements java.io.Serializable {
    private int transactionCount;

    private int fileID;

    private int resultCode;

    private java.lang.String resultDetail;

    public CreditCardBatchFileUploadResult() {
    }

    public CreditCardBatchFileUploadResult(
           int transactionCount,
           int fileID,
           int resultCode,
           java.lang.String resultDetail) {
           this.transactionCount = transactionCount;
           this.fileID = fileID;
           this.resultCode = resultCode;
           this.resultDetail = resultDetail;
    }


    /**
     * Gets the transactionCount value for this CreditCardBatchFileUploadResult.
     * 
     * @return transactionCount
     */
    public int getTransactionCount() {
        return transactionCount;
    }


    /**
     * Sets the transactionCount value for this CreditCardBatchFileUploadResult.
     * 
     * @param transactionCount
     */
    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }


    /**
     * Gets the fileID value for this CreditCardBatchFileUploadResult.
     * 
     * @return fileID
     */
    public int getFileID() {
        return fileID;
    }


    /**
     * Sets the fileID value for this CreditCardBatchFileUploadResult.
     * 
     * @param fileID
     */
    public void setFileID(int fileID) {
        this.fileID = fileID;
    }


    /**
     * Gets the resultCode value for this CreditCardBatchFileUploadResult.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this CreditCardBatchFileUploadResult.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the resultDetail value for this CreditCardBatchFileUploadResult.
     * 
     * @return resultDetail
     */
    public java.lang.String getResultDetail() {
        return resultDetail;
    }


    /**
     * Sets the resultDetail value for this CreditCardBatchFileUploadResult.
     * 
     * @param resultDetail
     */
    public void setResultDetail(java.lang.String resultDetail) {
        this.resultDetail = resultDetail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreditCardBatchFileUploadResult)) return false;
        CreditCardBatchFileUploadResult other = (CreditCardBatchFileUploadResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.transactionCount == other.getTransactionCount() &&
            this.fileID == other.getFileID() &&
            this.resultCode == other.getResultCode() &&
            ((this.resultDetail==null && other.getResultDetail()==null) || 
             (this.resultDetail!=null &&
              this.resultDetail.equals(other.getResultDetail())));
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
        _hashCode += getTransactionCount();
        _hashCode += getFileID();
        _hashCode += getResultCode();
        if (getResultDetail() != null) {
            _hashCode += getResultDetail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreditCardBatchFileUploadResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CreditCardBatchFileUploadResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionCount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TransactionCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "FileID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ResultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ResultDetail"));
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

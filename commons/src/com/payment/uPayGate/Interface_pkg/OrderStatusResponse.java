/**
 * OrderStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

public class OrderStatusResponse  implements java.io.Serializable {
    private String errorCode;

    private String errorMessage;

    private String errorDescription;

    private String result;

    private String transactionID;

    private String transactionErrorCode;

    private String transactionErrorMessage;

    private String transactionDate;

    public OrderStatusResponse() {
    }

    public OrderStatusResponse(
           String errorCode,
           String errorMessage,
           String errorDescription,
           String result,
           String transactionID,
           String transactionErrorCode,
           String transactionErrorMessage,
           String transactionDate) {
           this.errorCode = errorCode;
           this.errorMessage = errorMessage;
           this.errorDescription = errorDescription;
           this.result = result;
           this.transactionID = transactionID;
           this.transactionErrorCode = transactionErrorCode;
           this.transactionErrorMessage = transactionErrorMessage;
           this.transactionDate = transactionDate;
    }


    /**
     * Gets the errorCode value for this OrderStatusResponse.
     * 
     * @return errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this OrderStatusResponse.
     * 
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorMessage value for this OrderStatusResponse.
     * 
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this OrderStatusResponse.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the errorDescription value for this OrderStatusResponse.
     * 
     * @return errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }


    /**
     * Sets the errorDescription value for this OrderStatusResponse.
     * 
     * @param errorDescription
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }


    /**
     * Gets the result value for this OrderStatusResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this OrderStatusResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the transactionID value for this OrderStatusResponse.
     * 
     * @return transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this OrderStatusResponse.
     * 
     * @param transactionID
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the transactionErrorCode value for this OrderStatusResponse.
     * 
     * @return transactionErrorCode
     */
    public String getTransactionErrorCode() {
        return transactionErrorCode;
    }


    /**
     * Sets the transactionErrorCode value for this OrderStatusResponse.
     * 
     * @param transactionErrorCode
     */
    public void setTransactionErrorCode(String transactionErrorCode) {
        this.transactionErrorCode = transactionErrorCode;
    }


    /**
     * Gets the transactionErrorMessage value for this OrderStatusResponse.
     * 
     * @return transactionErrorMessage
     */
    public String getTransactionErrorMessage() {
        return transactionErrorMessage;
    }


    /**
     * Sets the transactionErrorMessage value for this OrderStatusResponse.
     * 
     * @param transactionErrorMessage
     */
    public void setTransactionErrorMessage(String transactionErrorMessage) {
        this.transactionErrorMessage = transactionErrorMessage;
    }


    /**
     * Gets the transactionDate value for this OrderStatusResponse.
     * 
     * @return transactionDate
     */
    public String getTransactionDate() {
        return transactionDate;
    }


    /**
     * Sets the transactionDate value for this OrderStatusResponse.
     * 
     * @param transactionDate
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof OrderStatusResponse)) return false;
        OrderStatusResponse other = (OrderStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            ((this.errorDescription==null && other.getErrorDescription()==null) || 
             (this.errorDescription!=null &&
              this.errorDescription.equals(other.getErrorDescription()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.transactionErrorCode==null && other.getTransactionErrorCode()==null) || 
             (this.transactionErrorCode!=null &&
              this.transactionErrorCode.equals(other.getTransactionErrorCode()))) &&
            ((this.transactionErrorMessage==null && other.getTransactionErrorMessage()==null) || 
             (this.transactionErrorMessage!=null &&
              this.transactionErrorMessage.equals(other.getTransactionErrorMessage()))) &&
            ((this.transactionDate==null && other.getTransactionDate()==null) || 
             (this.transactionDate!=null &&
              this.transactionDate.equals(other.getTransactionDate())));
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
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        if (getErrorDescription() != null) {
            _hashCode += getErrorDescription().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getTransactionErrorCode() != null) {
            _hashCode += getTransactionErrorCode().hashCode();
        }
        if (getTransactionErrorMessage() != null) {
            _hashCode += getTransactionErrorMessage().hashCode();
        }
        if (getTransactionDate() != null) {
            _hashCode += getTransactionDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OrderStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "OrderStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionErrorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionErrorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionErrorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionErrorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionDate"));
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

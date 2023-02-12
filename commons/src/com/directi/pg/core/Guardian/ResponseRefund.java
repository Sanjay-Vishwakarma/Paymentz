/**
 * ResponseRefund.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public class ResponseRefund  implements java.io.Serializable {
    private int refund_id;

    private int transaction_id;

    private float amount;

    private String result;

    private String result_text;

    private String result_code;

    public ResponseRefund() {
    }

    public ResponseRefund(
           int refund_id,
           int transaction_id,
           float amount,
           String result,
           String result_text,
           String result_code) {
           this.refund_id = refund_id;
           this.transaction_id = transaction_id;
           this.amount = amount;
           this.result = result;
           this.result_text = result_text;
           this.result_code = result_code;
    }


    /**
     * Gets the refund_id value for this ResponseRefund.
     * 
     * @return refund_id
     */
    public int getRefund_id() {
        return refund_id;
    }


    /**
     * Sets the refund_id value for this ResponseRefund.
     * 
     * @param refund_id
     */
    public void setRefund_id(int refund_id) {
        this.refund_id = refund_id;
    }


    /**
     * Gets the transaction_id value for this ResponseRefund.
     * 
     * @return transaction_id
     */
    public int getTransaction_id() {
        return transaction_id;
    }


    /**
     * Sets the transaction_id value for this ResponseRefund.
     * 
     * @param transaction_id
     */
    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }


    /**
     * Gets the amount value for this ResponseRefund.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ResponseRefund.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the result value for this ResponseRefund.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this ResponseRefund.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the result_text value for this ResponseRefund.
     * 
     * @return result_text
     */
    public String getResult_text() {
        return result_text;
    }


    /**
     * Sets the result_text value for this ResponseRefund.
     * 
     * @param result_text
     */
    public void setResult_text(String result_text) {
        this.result_text = result_text;
    }


    /**
     * Gets the result_code value for this ResponseRefund.
     * 
     * @return result_code
     */
    public String getResult_code() {
        return result_code;
    }


    /**
     * Sets the result_code value for this ResponseRefund.
     * 
     * @param result_code
     */
    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseRefund)) return false;
        ResponseRefund other = (ResponseRefund) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.refund_id == other.getRefund_id() &&
            this.transaction_id == other.getTransaction_id() &&
            this.amount == other.getAmount() &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.result_text==null && other.getResult_text()==null) || 
             (this.result_text!=null &&
              this.result_text.equals(other.getResult_text()))) &&
            ((this.result_code==null && other.getResult_code()==null) || 
             (this.result_code!=null &&
              this.result_code.equals(other.getResult_code())));
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
        _hashCode += getRefund_id();
        _hashCode += getTransaction_id();
        _hashCode += new Float(getAmount()).hashCode();
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getResult_text() != null) {
            _hashCode += getResult_text().hashCode();
        }
        if (getResult_code() != null) {
            _hashCode += getResult_code().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseRefund.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:ccgwwsdl", "ResponseRefund"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refund_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refund_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transaction_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result_text");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result_text"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result_code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result_code"));
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

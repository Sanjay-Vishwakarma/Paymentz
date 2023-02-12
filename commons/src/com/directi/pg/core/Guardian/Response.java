/**
 * Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public class Response  implements java.io.Serializable {
    private int transaction_id;

    private float transaction_amount;

    private String transaction_currency;

    private String result;

    private String result_text;

    private String result_code;

    private String descriptor;

    public Response() {
    }

    public Response(
           int transaction_id,
           float transaction_amount,
           String transaction_currency,
           String result,
           String result_text,
           String result_code,
           String descriptor) {
           this.transaction_id = transaction_id;
           this.transaction_amount = transaction_amount;
           this.transaction_currency = transaction_currency;
           this.result = result;
           this.result_text = result_text;
           this.result_code = result_code;
           this.descriptor = descriptor;
    }


    /**
     * Gets the transaction_id value for this Response.
     * 
     * @return transaction_id
     */
    public int getTransaction_id() {
        return transaction_id;
    }


    /**
     * Sets the transaction_id value for this Response.
     * 
     * @param transaction_id
     */
    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }


    /**
     * Gets the transaction_amount value for this Response.
     * 
     * @return transaction_amount
     */
    public float getTransaction_amount() {
        return transaction_amount;
    }


    /**
     * Sets the transaction_amount value for this Response.
     * 
     * @param transaction_amount
     */
    public void setTransaction_amount(float transaction_amount) {
        this.transaction_amount = transaction_amount;
    }


    /**
     * Gets the transaction_currency value for this Response.
     * 
     * @return transaction_currency
     */
    public String getTransaction_currency() {
        return transaction_currency;
    }


    /**
     * Sets the transaction_currency value for this Response.
     * 
     * @param transaction_currency
     */
    public void setTransaction_currency(String transaction_currency) {
        this.transaction_currency = transaction_currency;
    }


    /**
     * Gets the result value for this Response.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this Response.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the result_text value for this Response.
     * 
     * @return result_text
     */
    public String getResult_text() {
        return result_text;
    }


    /**
     * Sets the result_text value for this Response.
     * 
     * @param result_text
     */
    public void setResult_text(String result_text) {
        this.result_text = result_text;
    }


    /**
     * Gets the result_code value for this Response.
     * 
     * @return result_code
     */
    public String getResult_code() {
        return result_code;
    }


    /**
     * Sets the result_code value for this Response.
     * 
     * @param result_code
     */
    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }


    /**
     * Gets the descriptor value for this Response.
     * 
     * @return descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }


    /**
     * Sets the descriptor value for this Response.
     * 
     * @param descriptor
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Response)) return false;
        Response other = (Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.transaction_id == other.getTransaction_id() &&
            this.transaction_amount == other.getTransaction_amount() &&
            ((this.transaction_currency==null && other.getTransaction_currency()==null) || 
             (this.transaction_currency!=null &&
              this.transaction_currency.equals(other.getTransaction_currency()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.result_text==null && other.getResult_text()==null) || 
             (this.result_text!=null &&
              this.result_text.equals(other.getResult_text()))) &&
            ((this.result_code==null && other.getResult_code()==null) || 
             (this.result_code!=null &&
              this.result_code.equals(other.getResult_code()))) &&
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
        _hashCode += getTransaction_id();
        _hashCode += new Float(getTransaction_amount()).hashCode();
        if (getTransaction_currency() != null) {
            _hashCode += getTransaction_currency().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getResult_text() != null) {
            _hashCode += getResult_text().hashCode();
        }
        if (getResult_code() != null) {
            _hashCode += getResult_code().hashCode();
        }
        if (getDescriptor() != null) {
            _hashCode += getDescriptor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:ccgwwsdl", "Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transaction_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction_amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transaction_amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction_currency");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transaction_currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

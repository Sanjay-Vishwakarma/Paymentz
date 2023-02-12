/**
 * TTransacResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payGateway.core.message.paygateway.process;

public class TTransacResult  implements java.io.Serializable {
    private java.lang.String resp_trans_id;

    private java.lang.String resp_trans_merchant_id;

    private java.lang.String resp_trans_amount;

    private java.lang.String resp_action_type;

    private java.lang.String resp_trans_status;

    private java.lang.String resp_trans_detailled_status;

    private java.lang.String resp_trans_description_status;

    private java.lang.String resp_merchant_data1;

    private java.lang.String resp_merchant_data2;

    private java.lang.String resp_merchant_data3;

    private java.lang.String resp_merchant_data4;

    private java.lang.String resp_sha;

    private java.lang.String[] errorsDescriptionCodes;

    public TTransacResult() {
    }

    public TTransacResult(
           java.lang.String resp_trans_id,
           java.lang.String resp_trans_merchant_id,
           java.lang.String resp_trans_amount,
           java.lang.String resp_action_type,
           java.lang.String resp_trans_status,
           java.lang.String resp_trans_detailled_status,
           java.lang.String resp_trans_description_status,
           java.lang.String resp_merchant_data1,
           java.lang.String resp_merchant_data2,
           java.lang.String resp_merchant_data3,
           java.lang.String resp_merchant_data4,
           java.lang.String resp_sha,
           java.lang.String[] errorsDescriptionCodes) {
           this.resp_trans_id = resp_trans_id;
           this.resp_trans_merchant_id = resp_trans_merchant_id;
           this.resp_trans_amount = resp_trans_amount;
           this.resp_action_type = resp_action_type;
           this.resp_trans_status = resp_trans_status;
           this.resp_trans_detailled_status = resp_trans_detailled_status;
           this.resp_trans_description_status = resp_trans_description_status;
           this.resp_merchant_data1 = resp_merchant_data1;
           this.resp_merchant_data2 = resp_merchant_data2;
           this.resp_merchant_data3 = resp_merchant_data3;
           this.resp_merchant_data4 = resp_merchant_data4;
           this.resp_sha = resp_sha;
           this.errorsDescriptionCodes = errorsDescriptionCodes;
    }


    /**
     * Gets the resp_trans_id value for this TTransacResult.
     * 
     * @return resp_trans_id
     */
    public java.lang.String getResp_trans_id() {
        return resp_trans_id;
    }


    /**
     * Sets the resp_trans_id value for this TTransacResult.
     * 
     * @param resp_trans_id
     */
    public void setResp_trans_id(java.lang.String resp_trans_id) {
        this.resp_trans_id = resp_trans_id;
    }


    /**
     * Gets the resp_trans_merchant_id value for this TTransacResult.
     * 
     * @return resp_trans_merchant_id
     */
    public java.lang.String getResp_trans_merchant_id() {
        return resp_trans_merchant_id;
    }


    /**
     * Sets the resp_trans_merchant_id value for this TTransacResult.
     * 
     * @param resp_trans_merchant_id
     */
    public void setResp_trans_merchant_id(java.lang.String resp_trans_merchant_id) {
        this.resp_trans_merchant_id = resp_trans_merchant_id;
    }


    /**
     * Gets the resp_trans_amount value for this TTransacResult.
     * 
     * @return resp_trans_amount
     */
    public java.lang.String getResp_trans_amount() {
        return resp_trans_amount;
    }


    /**
     * Sets the resp_trans_amount value for this TTransacResult.
     * 
     * @param resp_trans_amount
     */
    public void setResp_trans_amount(java.lang.String resp_trans_amount) {
        this.resp_trans_amount = resp_trans_amount;
    }


    /**
     * Gets the resp_action_type value for this TTransacResult.
     * 
     * @return resp_action_type
     */
    public java.lang.String getResp_action_type() {
        return resp_action_type;
    }


    /**
     * Sets the resp_action_type value for this TTransacResult.
     * 
     * @param resp_action_type
     */
    public void setResp_action_type(java.lang.String resp_action_type) {
        this.resp_action_type = resp_action_type;
    }


    /**
     * Gets the resp_trans_status value for this TTransacResult.
     * 
     * @return resp_trans_status
     */
    public java.lang.String getResp_trans_status() {
        return resp_trans_status;
    }


    /**
     * Sets the resp_trans_status value for this TTransacResult.
     * 
     * @param resp_trans_status
     */
    public void setResp_trans_status(java.lang.String resp_trans_status) {
        this.resp_trans_status = resp_trans_status;
    }


    /**
     * Gets the resp_trans_detailled_status value for this TTransacResult.
     * 
     * @return resp_trans_detailled_status
     */
    public java.lang.String getResp_trans_detailled_status() {
        return resp_trans_detailled_status;
    }


    /**
     * Sets the resp_trans_detailled_status value for this TTransacResult.
     * 
     * @param resp_trans_detailled_status
     */
    public void setResp_trans_detailled_status(java.lang.String resp_trans_detailled_status) {
        this.resp_trans_detailled_status = resp_trans_detailled_status;
    }


    /**
     * Gets the resp_trans_description_status value for this TTransacResult.
     * 
     * @return resp_trans_description_status
     */
    public java.lang.String getResp_trans_description_status() {
        return resp_trans_description_status;
    }


    /**
     * Sets the resp_trans_description_status value for this TTransacResult.
     * 
     * @param resp_trans_description_status
     */
    public void setResp_trans_description_status(java.lang.String resp_trans_description_status) {
        this.resp_trans_description_status = resp_trans_description_status;
    }


    /**
     * Gets the resp_merchant_data1 value for this TTransacResult.
     * 
     * @return resp_merchant_data1
     */
    public java.lang.String getResp_merchant_data1() {
        return resp_merchant_data1;
    }


    /**
     * Sets the resp_merchant_data1 value for this TTransacResult.
     * 
     * @param resp_merchant_data1
     */
    public void setResp_merchant_data1(java.lang.String resp_merchant_data1) {
        this.resp_merchant_data1 = resp_merchant_data1;
    }


    /**
     * Gets the resp_merchant_data2 value for this TTransacResult.
     * 
     * @return resp_merchant_data2
     */
    public java.lang.String getResp_merchant_data2() {
        return resp_merchant_data2;
    }


    /**
     * Sets the resp_merchant_data2 value for this TTransacResult.
     * 
     * @param resp_merchant_data2
     */
    public void setResp_merchant_data2(java.lang.String resp_merchant_data2) {
        this.resp_merchant_data2 = resp_merchant_data2;
    }


    /**
     * Gets the resp_merchant_data3 value for this TTransacResult.
     * 
     * @return resp_merchant_data3
     */
    public java.lang.String getResp_merchant_data3() {
        return resp_merchant_data3;
    }


    /**
     * Sets the resp_merchant_data3 value for this TTransacResult.
     * 
     * @param resp_merchant_data3
     */
    public void setResp_merchant_data3(java.lang.String resp_merchant_data3) {
        this.resp_merchant_data3 = resp_merchant_data3;
    }


    /**
     * Gets the resp_merchant_data4 value for this TTransacResult.
     * 
     * @return resp_merchant_data4
     */
    public java.lang.String getResp_merchant_data4() {
        return resp_merchant_data4;
    }


    /**
     * Sets the resp_merchant_data4 value for this TTransacResult.
     * 
     * @param resp_merchant_data4
     */
    public void setResp_merchant_data4(java.lang.String resp_merchant_data4) {
        this.resp_merchant_data4 = resp_merchant_data4;
    }


    /**
     * Gets the resp_sha value for this TTransacResult.
     * 
     * @return resp_sha
     */
    public java.lang.String getResp_sha() {
        return resp_sha;
    }


    /**
     * Sets the resp_sha value for this TTransacResult.
     * 
     * @param resp_sha
     */
    public void setResp_sha(java.lang.String resp_sha) {
        this.resp_sha = resp_sha;
    }


    /**
     * Gets the errorsDescriptionCodes value for this TTransacResult.
     * 
     * @return errorsDescriptionCodes
     */
    public java.lang.String[] getErrorsDescriptionCodes() {
        return errorsDescriptionCodes;
    }


    /**
     * Sets the errorsDescriptionCodes value for this TTransacResult.
     * 
     * @param errorsDescriptionCodes
     */
    public void setErrorsDescriptionCodes(java.lang.String[] errorsDescriptionCodes) {
        this.errorsDescriptionCodes = errorsDescriptionCodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TTransacResult)) return false;
        TTransacResult other = (TTransacResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resp_trans_id==null && other.getResp_trans_id()==null) || 
             (this.resp_trans_id!=null &&
              this.resp_trans_id.equals(other.getResp_trans_id()))) &&
            ((this.resp_trans_merchant_id==null && other.getResp_trans_merchant_id()==null) || 
             (this.resp_trans_merchant_id!=null &&
              this.resp_trans_merchant_id.equals(other.getResp_trans_merchant_id()))) &&
            ((this.resp_trans_amount==null && other.getResp_trans_amount()==null) || 
             (this.resp_trans_amount!=null &&
              this.resp_trans_amount.equals(other.getResp_trans_amount()))) &&
            ((this.resp_action_type==null && other.getResp_action_type()==null) || 
             (this.resp_action_type!=null &&
              this.resp_action_type.equals(other.getResp_action_type()))) &&
            ((this.resp_trans_status==null && other.getResp_trans_status()==null) || 
             (this.resp_trans_status!=null &&
              this.resp_trans_status.equals(other.getResp_trans_status()))) &&
            ((this.resp_trans_detailled_status==null && other.getResp_trans_detailled_status()==null) || 
             (this.resp_trans_detailled_status!=null &&
              this.resp_trans_detailled_status.equals(other.getResp_trans_detailled_status()))) &&
            ((this.resp_trans_description_status==null && other.getResp_trans_description_status()==null) || 
             (this.resp_trans_description_status!=null &&
              this.resp_trans_description_status.equals(other.getResp_trans_description_status()))) &&
            ((this.resp_merchant_data1==null && other.getResp_merchant_data1()==null) || 
             (this.resp_merchant_data1!=null &&
              this.resp_merchant_data1.equals(other.getResp_merchant_data1()))) &&
            ((this.resp_merchant_data2==null && other.getResp_merchant_data2()==null) || 
             (this.resp_merchant_data2!=null &&
              this.resp_merchant_data2.equals(other.getResp_merchant_data2()))) &&
            ((this.resp_merchant_data3==null && other.getResp_merchant_data3()==null) || 
             (this.resp_merchant_data3!=null &&
              this.resp_merchant_data3.equals(other.getResp_merchant_data3()))) &&
            ((this.resp_merchant_data4==null && other.getResp_merchant_data4()==null) || 
             (this.resp_merchant_data4!=null &&
              this.resp_merchant_data4.equals(other.getResp_merchant_data4()))) &&
            ((this.resp_sha==null && other.getResp_sha()==null) || 
             (this.resp_sha!=null &&
              this.resp_sha.equals(other.getResp_sha()))) &&
            ((this.errorsDescriptionCodes==null && other.getErrorsDescriptionCodes()==null) || 
             (this.errorsDescriptionCodes!=null &&
              java.util.Arrays.equals(this.errorsDescriptionCodes, other.getErrorsDescriptionCodes())));
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
        if (getResp_trans_id() != null) {
            _hashCode += getResp_trans_id().hashCode();
        }
        if (getResp_trans_merchant_id() != null) {
            _hashCode += getResp_trans_merchant_id().hashCode();
        }
        if (getResp_trans_amount() != null) {
            _hashCode += getResp_trans_amount().hashCode();
        }
        if (getResp_action_type() != null) {
            _hashCode += getResp_action_type().hashCode();
        }
        if (getResp_trans_status() != null) {
            _hashCode += getResp_trans_status().hashCode();
        }
        if (getResp_trans_detailled_status() != null) {
            _hashCode += getResp_trans_detailled_status().hashCode();
        }
        if (getResp_trans_description_status() != null) {
            _hashCode += getResp_trans_description_status().hashCode();
        }
        if (getResp_merchant_data1() != null) {
            _hashCode += getResp_merchant_data1().hashCode();
        }
        if (getResp_merchant_data2() != null) {
            _hashCode += getResp_merchant_data2().hashCode();
        }
        if (getResp_merchant_data3() != null) {
            _hashCode += getResp_merchant_data3().hashCode();
        }
        if (getResp_merchant_data4() != null) {
            _hashCode += getResp_merchant_data4().hashCode();
        }
        if (getResp_sha() != null) {
            _hashCode += getResp_sha().hashCode();
        }
        if (getErrorsDescriptionCodes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErrorsDescriptionCodes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErrorsDescriptionCodes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TTransacResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://paygateway.net/", "TTransacResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_merchant_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_merchant_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_action_type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_action_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_detailled_status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_detailled_status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_trans_description_status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_trans_description_status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_merchant_data1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_merchant_data1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_merchant_data2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_merchant_data2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_merchant_data3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_merchant_data3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_merchant_data4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_merchant_data4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resp_sha");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "resp_sha"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorsDescriptionCodes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://paygateway.net/", "ErrorsDescriptionCodes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://paygateway.net/", "string"));
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

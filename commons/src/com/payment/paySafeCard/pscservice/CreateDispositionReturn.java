/**
 * CreateDispositionReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class CreateDispositionReturn  implements java.io.Serializable {
    private java.lang.String mtid;

    private java.lang.String[] subId;

    private java.lang.String mid;

    private int resultCode;

    private int errorCode;

    public CreateDispositionReturn() {
    }

    public CreateDispositionReturn(
           java.lang.String mtid,
           java.lang.String[] subId,
           java.lang.String mid,
           int resultCode,
           int errorCode) {
           this.mtid = mtid;
           this.subId = subId;
           this.mid = mid;
           this.resultCode = resultCode;
           this.errorCode = errorCode;
    }


    /**
     * Gets the mtid value for this CreateDispositionReturn.
     * 
     * @return mtid
     */
    public java.lang.String getMtid() {
        return mtid;
    }


    /**
     * Sets the mtid value for this CreateDispositionReturn.
     * 
     * @param mtid
     */
    public void setMtid(java.lang.String mtid) {
        this.mtid = mtid;
    }


    /**
     * Gets the subId value for this CreateDispositionReturn.
     * 
     * @return subId
     */
    public java.lang.String[] getSubId() {
        return subId;
    }


    /**
     * Sets the subId value for this CreateDispositionReturn.
     * 
     * @param subId
     */
    public void setSubId(java.lang.String[] subId) {
        this.subId = subId;
    }

    public java.lang.String getSubId(int i) {
        return this.subId[i];
    }

    public void setSubId(int i, java.lang.String _value) {
        this.subId[i] = _value;
    }


    /**
     * Gets the mid value for this CreateDispositionReturn.
     * 
     * @return mid
     */
    public java.lang.String getMid() {
        return mid;
    }


    /**
     * Sets the mid value for this CreateDispositionReturn.
     * 
     * @param mid
     */
    public void setMid(java.lang.String mid) {
        this.mid = mid;
    }


    /**
     * Gets the resultCode value for this CreateDispositionReturn.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this CreateDispositionReturn.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this CreateDispositionReturn.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this CreateDispositionReturn.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreateDispositionReturn)) return false;
        CreateDispositionReturn other = (CreateDispositionReturn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mtid==null && other.getMtid()==null) || 
             (this.mtid!=null &&
              this.mtid.equals(other.getMtid()))) &&
            ((this.subId==null && other.getSubId()==null) || 
             (this.subId!=null &&
              java.util.Arrays.equals(this.subId, other.getSubId()))) &&
            ((this.mid==null && other.getMid()==null) || 
             (this.mid!=null &&
              this.mid.equals(other.getMid()))) &&
            this.resultCode == other.getResultCode() &&
            this.errorCode == other.getErrorCode();
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
        if (getMtid() != null) {
            _hashCode += getMtid().hashCode();
        }
        if (getSubId() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubId());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubId(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMid() != null) {
            _hashCode += getMid().hashCode();
        }
        _hashCode += getResultCode();
        _hashCode += getErrorCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreateDispositionReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "CreateDispositionReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mtid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mtid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "subId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "resultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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

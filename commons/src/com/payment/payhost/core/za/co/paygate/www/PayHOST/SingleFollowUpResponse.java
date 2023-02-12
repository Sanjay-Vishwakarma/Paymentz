/**
 * SingleFollowUpResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SingleFollowUpResponse  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType[] queryResponse;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleResponseType settlementResponse;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundResponseType refundResponse;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidResponseType voidResponse;

    public SingleFollowUpResponse() {
    }

    public SingleFollowUpResponse(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType[] queryResponse,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleResponseType settlementResponse,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundResponseType refundResponse,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidResponseType voidResponse) {
           this.queryResponse = queryResponse;
           this.settlementResponse = settlementResponse;
           this.refundResponse = refundResponse;
           this.voidResponse = voidResponse;
    }


    /**
     * Gets the queryResponse value for this SingleFollowUpResponse.
     * 
     * @return queryResponse
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType[] getQueryResponse() {
        return queryResponse;
    }


    /**
     * Sets the queryResponse value for this SingleFollowUpResponse.
     * 
     * @param queryResponse
     */
    public void setQueryResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType[] queryResponse) {
        this.queryResponse = queryResponse;
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType getQueryResponse(int i) {
        return this.queryResponse[i];
    }

    public void setQueryResponse(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType _value) {
        this.queryResponse[i] = _value;
    }


    /**
     * Gets the settlementResponse value for this SingleFollowUpResponse.
     * 
     * @return settlementResponse
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleResponseType getSettlementResponse() {
        return settlementResponse;
    }


    /**
     * Sets the settlementResponse value for this SingleFollowUpResponse.
     * 
     * @param settlementResponse
     */
    public void setSettlementResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleResponseType settlementResponse) {
        this.settlementResponse = settlementResponse;
    }


    /**
     * Gets the refundResponse value for this SingleFollowUpResponse.
     * 
     * @return refundResponse
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundResponseType getRefundResponse() {
        return refundResponse;
    }


    /**
     * Sets the refundResponse value for this SingleFollowUpResponse.
     * 
     * @param refundResponse
     */
    public void setRefundResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundResponseType refundResponse) {
        this.refundResponse = refundResponse;
    }


    /**
     * Gets the voidResponse value for this SingleFollowUpResponse.
     * 
     * @return voidResponse
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidResponseType getVoidResponse() {
        return voidResponse;
    }


    /**
     * Sets the voidResponse value for this SingleFollowUpResponse.
     * 
     * @param voidResponse
     */
    public void setVoidResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidResponseType voidResponse) {
        this.voidResponse = voidResponse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SingleFollowUpResponse)) return false;
        SingleFollowUpResponse other = (SingleFollowUpResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.queryResponse==null && other.getQueryResponse()==null) || 
             (this.queryResponse!=null &&
              java.util.Arrays.equals(this.queryResponse, other.getQueryResponse()))) &&
            ((this.settlementResponse==null && other.getSettlementResponse()==null) || 
             (this.settlementResponse!=null &&
              this.settlementResponse.equals(other.getSettlementResponse()))) &&
            ((this.refundResponse==null && other.getRefundResponse()==null) || 
             (this.refundResponse!=null &&
              this.refundResponse.equals(other.getRefundResponse()))) &&
            ((this.voidResponse==null && other.getVoidResponse()==null) || 
             (this.voidResponse!=null &&
              this.voidResponse.equals(other.getVoidResponse())));
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
        if (getQueryResponse() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQueryResponse());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQueryResponse(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSettlementResponse() != null) {
            _hashCode += getSettlementResponse().hashCode();
        }
        if (getRefundResponse() != null) {
            _hashCode += getRefundResponse().hashCode();
        }
        if (getVoidResponse() != null) {
            _hashCode += getVoidResponse().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SingleFollowUpResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settlementResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettlementResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettleResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refundResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidResponseType"));
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

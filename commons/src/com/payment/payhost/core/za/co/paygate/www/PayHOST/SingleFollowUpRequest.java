/**
 * SingleFollowUpRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SingleFollowUpRequest  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryRequestType queryRequest;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleRequestType settlementRequest;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundRequestType refundRequest;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidRequestType voidRequest;

    public SingleFollowUpRequest() {
    }

    public SingleFollowUpRequest(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryRequestType queryRequest,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleRequestType settlementRequest,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundRequestType refundRequest,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidRequestType voidRequest) {
           this.queryRequest = queryRequest;
           this.settlementRequest = settlementRequest;
           this.refundRequest = refundRequest;
           this.voidRequest = voidRequest;
    }


    /**
     * Gets the queryRequest value for this SingleFollowUpRequest.
     * 
     * @return queryRequest
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryRequestType getQueryRequest() {
        return queryRequest;
    }


    /**
     * Sets the queryRequest value for this SingleFollowUpRequest.
     * 
     * @param queryRequest
     */
    public void setQueryRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryRequestType queryRequest) {
        this.queryRequest = queryRequest;
    }


    /**
     * Gets the settlementRequest value for this SingleFollowUpRequest.
     * 
     * @return settlementRequest
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleRequestType getSettlementRequest() {
        return settlementRequest;
    }


    /**
     * Sets the settlementRequest value for this SingleFollowUpRequest.
     * 
     * @param settlementRequest
     */
    public void setSettlementRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleRequestType settlementRequest) {
        this.settlementRequest = settlementRequest;
    }


    /**
     * Gets the refundRequest value for this SingleFollowUpRequest.
     * 
     * @return refundRequest
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundRequestType getRefundRequest() {
        return refundRequest;
    }


    /**
     * Sets the refundRequest value for this SingleFollowUpRequest.
     * 
     * @param refundRequest
     */
    public void setRefundRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundRequestType refundRequest) {
        this.refundRequest = refundRequest;
    }


    /**
     * Gets the voidRequest value for this SingleFollowUpRequest.
     * 
     * @return voidRequest
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidRequestType getVoidRequest() {
        return voidRequest;
    }


    /**
     * Sets the voidRequest value for this SingleFollowUpRequest.
     * 
     * @param voidRequest
     */
    public void setVoidRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidRequestType voidRequest) {
        this.voidRequest = voidRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SingleFollowUpRequest)) return false;
        SingleFollowUpRequest other = (SingleFollowUpRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.queryRequest==null && other.getQueryRequest()==null) || 
             (this.queryRequest!=null &&
              this.queryRequest.equals(other.getQueryRequest()))) &&
            ((this.settlementRequest==null && other.getSettlementRequest()==null) || 
             (this.settlementRequest!=null &&
              this.settlementRequest.equals(other.getSettlementRequest()))) &&
            ((this.refundRequest==null && other.getRefundRequest()==null) || 
             (this.refundRequest!=null &&
              this.refundRequest.equals(other.getRefundRequest()))) &&
            ((this.voidRequest==null && other.getVoidRequest()==null) || 
             (this.voidRequest!=null &&
              this.voidRequest.equals(other.getVoidRequest())));
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
        if (getQueryRequest() != null) {
            _hashCode += getQueryRequest().hashCode();
        }
        if (getSettlementRequest() != null) {
            _hashCode += getSettlementRequest().hashCode();
        }
        if (getRefundRequest() != null) {
            _hashCode += getRefundRequest().hashCode();
        }
        if (getVoidRequest() != null) {
            _hashCode += getVoidRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SingleFollowUpRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settlementRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettlementRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettleRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refundRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidRequestType"));
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

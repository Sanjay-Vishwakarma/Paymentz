/**
 * SinglePaymentRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SinglePaymentRequest  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentRequestType cardPaymentRequest;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentRequestType webPaymentRequest;

    public SinglePaymentRequest() {
    }

    public SinglePaymentRequest(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentRequestType cardPaymentRequest)
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentRequestType webPaymentRequest)
    {
           this.cardPaymentRequest = cardPaymentRequest;
           //this.webPaymentRequest = webPaymentRequest;
    }


    /**
     * Gets the cardPaymentRequest value for this SinglePaymentRequest.
     * 
     * @return cardPaymentRequest
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentRequestType getCardPaymentRequest() {
        return cardPaymentRequest;
    }


    /**
     * Sets the cardPaymentRequest value for this SinglePaymentRequest.
     * 
     * @param cardPaymentRequest
     */
    public void setCardPaymentRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentRequestType cardPaymentRequest) {
        this.cardPaymentRequest = cardPaymentRequest;
    }


    /**
     * Gets the webPaymentRequest value for this SinglePaymentRequest.
     * 
     * @return webPaymentRequest
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentRequestType getWebPaymentRequest() {
        return webPaymentRequest;
    }
*/

    /**
     * Sets the webPaymentRequest value for this SinglePaymentRequest.
     * 
     * @param webPaymentRequest
     */
    /*public void setWebPaymentRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentRequestType webPaymentRequest) {
        this.webPaymentRequest = webPaymentRequest;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SinglePaymentRequest)) return false;
        SinglePaymentRequest other = (SinglePaymentRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cardPaymentRequest==null && other.getCardPaymentRequest()==null) || 
             (this.cardPaymentRequest!=null &&
              this.cardPaymentRequest.equals(other.getCardPaymentRequest())))/* &&
            ((this.webPaymentRequest==null && other.getWebPaymentRequest()==null) || 
             (this.webPaymentRequest!=null &&
              this.webPaymentRequest.equals(other.getWebPaymentRequest())))*/;
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
        if (getCardPaymentRequest() != null) {
            _hashCode += getCardPaymentRequest().hashCode();
        }
        /*if (getWebPaymentRequest() != null) {
            _hashCode += getWebPaymentRequest().hashCode();
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SinglePaymentRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPaymentRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("webPaymentRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentRequestType"));
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

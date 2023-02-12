/**
 * SinglePaymentResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SinglePaymentResponse  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentResponseType cardPaymentResponse;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentResponseType webPaymentResponse;

    public SinglePaymentResponse() {
    }

    public SinglePaymentResponse(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentResponseType cardPaymentResponse)
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentResponseType webPaymentResponse)
    {
           this.cardPaymentResponse = cardPaymentResponse;
           //this.webPaymentResponse = webPaymentResponse;
    }


    /**
     * Gets the cardPaymentResponse value for this SinglePaymentResponse.
     * 
     * @return cardPaymentResponse
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentResponseType getCardPaymentResponse() {
        return cardPaymentResponse;
    }


    /**
     * Sets the cardPaymentResponse value for this SinglePaymentResponse.
     * 
     * @param cardPaymentResponse
     */
    public void setCardPaymentResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentResponseType cardPaymentResponse) {
        this.cardPaymentResponse = cardPaymentResponse;
    }


    /**
     * Gets the webPaymentResponse value for this SinglePaymentResponse.
     * 
     * @return webPaymentResponse
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentResponseType getWebPaymentResponse() {
        return webPaymentResponse;
    }
*/

    /**
     * Sets the webPaymentResponse value for this SinglePaymentResponse.
     * 
     * @param webPaymentResponse
     */
    /*public void setWebPaymentResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentResponseType webPaymentResponse) {
        this.webPaymentResponse = webPaymentResponse;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SinglePaymentResponse)) return false;
        SinglePaymentResponse other = (SinglePaymentResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cardPaymentResponse==null && other.getCardPaymentResponse()==null) || 
             (this.cardPaymentResponse!=null &&
              this.cardPaymentResponse.equals(other.getCardPaymentResponse()))) /*&&
            ((this.webPaymentResponse==null && other.getWebPaymentResponse()==null) || 
             (this.webPaymentResponse!=null &&
              this.webPaymentResponse.equals(other.getWebPaymentResponse())))*/;
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
        if (getCardPaymentResponse() != null) {
            _hashCode += getCardPaymentResponse().hashCode();
        }
        /*if (getWebPaymentResponse() != null) {
            _hashCode += getWebPaymentResponse().hashCode();
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SinglePaymentResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPaymentResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("webPaymentResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentResponseType"));
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

/**
 * SinglePayoutRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SinglePayoutRequest  implements java.io.Serializable {
    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutRequestType cardPayoutRequest;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutRequestType bankPayoutRequest;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutRequestType walletPayoutRequest;

    public SinglePayoutRequest() {
    }

    /*public SinglePayoutRequest(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutRequestType cardPayoutRequest,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutRequestType bankPayoutRequest,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutRequestType walletPayoutRequest) {
           this.cardPayoutRequest = cardPayoutRequest;
           this.bankPayoutRequest = bankPayoutRequest;
           this.walletPayoutRequest = walletPayoutRequest;
    }*/


    /**
     * Gets the cardPayoutRequest value for this SinglePayoutRequest.
     * 
     * @return cardPayoutRequest
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutRequestType getCardPayoutRequest() {
        return cardPayoutRequest;
    }*/


    /**
     * Sets the cardPayoutRequest value for this SinglePayoutRequest.
     * 
     * @param cardPayoutRequest
     */
    /*public void setCardPayoutRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutRequestType cardPayoutRequest) {
        this.cardPayoutRequest = cardPayoutRequest;
    }
*/

    /**
     * Gets the bankPayoutRequest value for this SinglePayoutRequest.
     * 
     * @return bankPayoutRequest
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutRequestType getBankPayoutRequest() {
        return bankPayoutRequest;
    }
*/

    /**
     * Sets the bankPayoutRequest value for this SinglePayoutRequest.
     * 
     * @param bankPayoutRequest
     */
    /*public void setBankPayoutRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutRequestType bankPayoutRequest) {
        this.bankPayoutRequest = bankPayoutRequest;
    }
*/

    /**
     * Gets the walletPayoutRequest value for this SinglePayoutRequest.
     * 
     * @return walletPayoutRequest
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutRequestType getWalletPayoutRequest() {
        return walletPayoutRequest;
    }
*/

    /**
     * Sets the walletPayoutRequest value for this SinglePayoutRequest.
     * 
     * @param walletPayoutRequest
     */
    /*public void setWalletPayoutRequest(com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutRequestType walletPayoutRequest) {
        this.walletPayoutRequest = walletPayoutRequest;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SinglePayoutRequest)) return false;
        SinglePayoutRequest other = (SinglePayoutRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true /*&&
            ((this.cardPayoutRequest==null && other.getCardPayoutRequest()==null) || 
             (this.cardPayoutRequest!=null &&
              this.cardPayoutRequest.equals(other.getCardPayoutRequest()))) &&
            ((this.bankPayoutRequest==null && other.getBankPayoutRequest()==null) || 
             (this.bankPayoutRequest!=null &&
              this.bankPayoutRequest.equals(other.getBankPayoutRequest()))) &&
            ((this.walletPayoutRequest==null && other.getWalletPayoutRequest()==null) || 
             (this.walletPayoutRequest!=null &&
              this.walletPayoutRequest.equals(other.getWalletPayoutRequest())))*/;
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
        /*if (getCardPayoutRequest() != null) {
            _hashCode += getCardPayoutRequest().hashCode();
        }
        if (getBankPayoutRequest() != null) {
            _hashCode += getBankPayoutRequest().hashCode();
        }
        if (getWalletPayoutRequest() != null) {
            _hashCode += getWalletPayoutRequest().hashCode();
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SinglePayoutRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPayoutRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankPayoutRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("walletPayoutRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutRequestType"));
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

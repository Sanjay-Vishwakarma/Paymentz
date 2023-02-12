/**
 * SinglePayoutResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class SinglePayoutResponse  implements java.io.Serializable {
    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutResponseType cardPayoutResponse;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutResponseType bankPayoutResponse;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutResponseType walletPayoutResponse;

    public SinglePayoutResponse() {
    }

    /*public SinglePayoutResponse(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutResponseType cardPayoutResponse,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutResponseType bankPayoutResponse,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutResponseType walletPayoutResponse) {
           this.cardPayoutResponse = cardPayoutResponse;
           this.bankPayoutResponse = bankPayoutResponse;
           this.walletPayoutResponse = walletPayoutResponse;
    }
*/

    /**
     * Gets the cardPayoutResponse value for this SinglePayoutResponse.
     * 
     * @return cardPayoutResponse
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutResponseType getCardPayoutResponse() {
        return cardPayoutResponse;
    }
*/

    /**
     * Sets the cardPayoutResponse value for this SinglePayoutResponse.
     * 
     * @param cardPayoutResponse
     */
    /*public void setCardPayoutResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutResponseType cardPayoutResponse) {
        this.cardPayoutResponse = cardPayoutResponse;
    }
*/

    /**
     * Sets the bankPayoutResponse value for this SinglePayoutResponse.
     *
     * @param bankPayoutResponse
     */
    /*public void setBankPayoutResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutResponseType bankPayoutResponse) {
        this.bankPayoutResponse = bankPayoutResponse;
    }*/


    /**
     * Gets the walletPayoutResponse value for this SinglePayoutResponse.
     *
     * @return walletPayoutResponse
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutResponseType getWalletPayoutResponse() {
        return walletPayoutResponse;
    }
*/

    /**
     * Sets the walletPayoutResponse value for this SinglePayoutResponse.
     *
     * @param walletPayoutResponse
     */
    /*public void setWalletPayoutResponse(com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutResponseType walletPayoutResponse) {
        this.walletPayoutResponse = walletPayoutResponse;
    }
*/

    /**
     * Gets the bankPayoutResponse value for this SinglePayoutResponse.
     *
     * @return bankPayoutResponse
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutResponseType getBankPayoutResponse() {
        return bankPayoutResponse;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SinglePayoutResponse)) return false;
        SinglePayoutResponse other = (SinglePayoutResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        __equalsCalc = null;
        _equals = true; /*&&
                ((this.cardPayoutResponse==null && other.getCardPayoutResponse()==null) ||
                        (this.cardPayoutResponse!=null &&
                                this.cardPayoutResponse.equals(other.getCardPayoutResponse()))) &&
                ((this.bankPayoutResponse==null && other.getBankPayoutResponse()==null) ||
                        (this.bankPayoutResponse!=null &&
                                this.bankPayoutResponse.equals(other.getBankPayoutResponse()))) &&
                ((this.walletPayoutResponse==null && other.getWalletPayoutResponse()==null) ||
                        (this.walletPayoutResponse!=null &&
                                this.walletPayoutResponse.equals(other.getWalletPayoutResponse())));*/
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        /*if (getCardPayoutResponse() != null) {
            _hashCode += getCardPayoutResponse().hashCode();
        }
        if (getBankPayoutResponse() != null) {
            _hashCode += getBankPayoutResponse().hashCode();
        }
        if (getWalletPayoutResponse() != null) {
            _hashCode += getWalletPayoutResponse().hashCode();
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SinglePayoutResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPayoutResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankPayoutResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutResponseType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("walletPayoutResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutResponseType"));
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

/**
 * CardVaultRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class CardVaultRequestType  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account;

    private org.apache.axis.types.Token cardNumber;

    private org.apache.axis.types.Token cardExpiryDate;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields;

    public CardVaultRequestType() {
    }

    public CardVaultRequestType(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account,
           org.apache.axis.types.Token cardNumber,
           org.apache.axis.types.Token cardExpiryDate)
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields)
    {
           this.account = account;
           this.cardNumber = cardNumber;
           this.cardExpiryDate = cardExpiryDate;
           //this.userDefinedFields = userDefinedFields;
    }


    /**
     * Gets the account value for this CardVaultRequestType.
     * 
     * @return account
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType getAccount() {
        return account;
    }


    /**
     * Sets the account value for this CardVaultRequestType.
     * 
     * @param account
     */
    public void setAccount(com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account) {
        this.account = account;
    }


    /**
     * Gets the cardNumber value for this CardVaultRequestType.
     * 
     * @return cardNumber
     */
    public org.apache.axis.types.Token getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this CardVaultRequestType.
     * 
     * @param cardNumber
     */
    public void setCardNumber(org.apache.axis.types.Token cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the cardExpiryDate value for this CardVaultRequestType.
     * 
     * @return cardExpiryDate
     */
    public org.apache.axis.types.Token getCardExpiryDate() {
        return cardExpiryDate;
    }


    /**
     * Sets the cardExpiryDate value for this CardVaultRequestType.
     * 
     * @param cardExpiryDate
     */
    public void setCardExpiryDate(org.apache.axis.types.Token cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }


    /**
     * Gets the userDefinedFields value for this CardVaultRequestType.
     * 
     * @return userDefinedFields
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] getUserDefinedFields() {
        return userDefinedFields;
    }
*/

    /*public void setUserDefinedFields(com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields) {
        this.userDefinedFields = userDefinedFields;
    }
*/
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType getUserDefinedFields(int i) {
        return this.userDefinedFields[i];
    }
*/
    /*public void setUserDefinedFields(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType _value) {
        this.userDefinedFields[i] = _value;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CardVaultRequestType)) return false;
        CardVaultRequestType other = (CardVaultRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.account==null && other.getAccount()==null) || 
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.cardExpiryDate==null && other.getCardExpiryDate()==null) || 
             (this.cardExpiryDate!=null &&
              this.cardExpiryDate.equals(other.getCardExpiryDate()))) /*&&
            ((this.userDefinedFields==null && other.getUserDefinedFields()==null) || 
             (this.userDefinedFields!=null &&
              java.util.Arrays.equals(this.userDefinedFields, other.getUserDefinedFields())))*/;
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
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getCardExpiryDate() != null) {
            _hashCode += getCardExpiryDate().hashCode();
        }
        /*if (getUserDefinedFields() != null)
        {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUserDefinedFields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUserDefinedFields(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CardVaultRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardVaultRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayGateAccountType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardExpiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userDefinedFields");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "UserDefinedFields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "KeyValueType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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

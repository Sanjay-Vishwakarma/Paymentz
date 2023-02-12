/**
 * MPIVERequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class MPIVERequest  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MPIVERequest.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MPIVERequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authentication");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Authentication"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.AuthenticationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("card");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Card"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MPIVERequest.CardType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Transaction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MPIVERequest.TransactionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestCardType card;
    private String token;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestTransactionType transaction;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public MPIVERequest() {
    }


    public MPIVERequest(
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestCardType card,
           String token,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestTransactionType transaction) {
           this.authentication = authentication;
           this.card = card;
           this.token = token;
           this.transaction = transaction;
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

    /**
     * Gets the authentication value for this MPIVERequest.
     *
     * @return authentication
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType getAuthentication() {
        return authentication;
    }

    /**
     * Sets the authentication value for this MPIVERequest.
     *
     * @param authentication
     */
    public void setAuthentication(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication) {
        this.authentication = authentication;
    }

    /**
     * Gets the card value for this MPIVERequest.
     *
     * @return card
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestCardType getCard() {
        return card;
    }

    /**
     * Sets the card value for this MPIVERequest.
     *
     * @param card
     */
    public void setCard(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestCardType card) {
        this.card = card;
    }

    /**
     * Gets the token value for this MPIVERequest.
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token value for this MPIVERequest.
     *
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the transaction value for this MPIVERequest.
     *
     * @return transaction
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestTransactionType getTransaction() {
        return transaction;
    }

    /**
     * Sets the transaction value for this MPIVERequest.
     *
     * @param transaction
     */
    public void setTransaction(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.MPIVERequestTransactionType transaction) {
        this.transaction = transaction;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MPIVERequest)) return false;
        MPIVERequest other = (MPIVERequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.authentication==null && other.getAuthentication()==null) ||
             (this.authentication!=null &&
              this.authentication.equals(other.getAuthentication()))) &&
            ((this.card==null && other.getCard()==null) ||
             (this.card!=null &&
              this.card.equals(other.getCard()))) &&
            ((this.token==null && other.getToken()==null) ||
             (this.token!=null &&
              this.token.equals(other.getToken()))) &&
            ((this.transaction==null && other.getTransaction()==null) ||
             (this.transaction!=null &&
              this.transaction.equals(other.getTransaction())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAuthentication() != null) {
            _hashCode += getAuthentication().hashCode();
        }
        if (getCard() != null) {
            _hashCode += getCard().hashCode();
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        if (getTransaction() != null) {
            _hashCode += getTransaction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

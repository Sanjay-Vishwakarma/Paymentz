/**
 * RequestTransactionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestTransactionType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestTransactionType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.TransactionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dynamicDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "DynamicDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Terminal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verbatimDynamicDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "VerbatimDynamicDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
    private String account;
    private org.apache.axis.types.UnsignedInt amount;
    private String currency;
    private String dynamicDescriptor;
    private String terminal;
    private Boolean verbatimDynamicDescriptor;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestTransactionType() {
    }


    public RequestTransactionType(
           String account,
           org.apache.axis.types.UnsignedInt amount,
           String currency,
           String dynamicDescriptor,
           String terminal,
           Boolean verbatimDynamicDescriptor) {
           this.account = account;
           this.amount = amount;
           this.currency = currency;
           this.dynamicDescriptor = dynamicDescriptor;
           this.terminal = terminal;
           this.verbatimDynamicDescriptor = verbatimDynamicDescriptor;
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
     * Gets the account value for this RequestTransactionType.
     *
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account value for this RequestTransactionType.
     *
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Gets the amount value for this RequestTransactionType.
     *
     * @return amount
     */
    public org.apache.axis.types.UnsignedInt getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this RequestTransactionType.
     *
     * @param amount
     */
    public void setAmount(org.apache.axis.types.UnsignedInt amount) {
        this.amount = amount;
    }

    /**
     * Gets the currency value for this RequestTransactionType.
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency value for this RequestTransactionType.
     *
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the dynamicDescriptor value for this RequestTransactionType.
     *
     * @return dynamicDescriptor
     */
    public String getDynamicDescriptor() {
        return dynamicDescriptor;
    }

    /**
     * Sets the dynamicDescriptor value for this RequestTransactionType.
     *
     * @param dynamicDescriptor
     */
    public void setDynamicDescriptor(String dynamicDescriptor) {
        this.dynamicDescriptor = dynamicDescriptor;
    }

    /**
     * Gets the terminal value for this RequestTransactionType.
     *
     * @return terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal value for this RequestTransactionType.
     *
     * @param terminal
     */
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    /**
     * Gets the verbatimDynamicDescriptor value for this RequestTransactionType.
     *
     * @return verbatimDynamicDescriptor
     */
    public Boolean getVerbatimDynamicDescriptor() {
        return verbatimDynamicDescriptor;
    }

    /**
     * Sets the verbatimDynamicDescriptor value for this RequestTransactionType.
     *
     * @param verbatimDynamicDescriptor
     */
    public void setVerbatimDynamicDescriptor(Boolean verbatimDynamicDescriptor) {
        this.verbatimDynamicDescriptor = verbatimDynamicDescriptor;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestTransactionType)) return false;
        RequestTransactionType other = (RequestTransactionType) obj;
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
            ((this.amount==null && other.getAmount()==null) ||
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.currency==null && other.getCurrency()==null) ||
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.dynamicDescriptor==null && other.getDynamicDescriptor()==null) ||
             (this.dynamicDescriptor!=null &&
              this.dynamicDescriptor.equals(other.getDynamicDescriptor()))) &&
            ((this.terminal==null && other.getTerminal()==null) ||
             (this.terminal!=null &&
              this.terminal.equals(other.getTerminal()))) &&
            ((this.verbatimDynamicDescriptor==null && other.getVerbatimDynamicDescriptor()==null) ||
             (this.verbatimDynamicDescriptor!=null &&
              this.verbatimDynamicDescriptor.equals(other.getVerbatimDynamicDescriptor())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getDynamicDescriptor() != null) {
            _hashCode += getDynamicDescriptor().hashCode();
        }
        if (getTerminal() != null) {
            _hashCode += getTerminal().hashCode();
        }
        if (getVerbatimDynamicDescriptor() != null) {
            _hashCode += getVerbatimDynamicDescriptor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

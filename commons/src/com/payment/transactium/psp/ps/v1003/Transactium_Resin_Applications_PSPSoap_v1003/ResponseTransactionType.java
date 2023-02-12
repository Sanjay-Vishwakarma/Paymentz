/**
 * ResponseTransactionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseTransactionType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseTransactionType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.TransactionType"));
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
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "AuthenticationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BatchId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BatchNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CVV2Result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CVV2Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.TransactionType.CVV2Check"));
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
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Id"));
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
    }
    private String account;
    private org.apache.axis.types.UnsignedInt amount;
    private String authenticationCode;
    private org.apache.axis.types.UnsignedInt batchId;
    private String batchNumber;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseTransactionTypeCVV2Check CVV2Result;
    private String currency;
    private String id;
    private String terminal;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public ResponseTransactionType() {
    }


    public ResponseTransactionType(
           String account,
           org.apache.axis.types.UnsignedInt amount,
           String authenticationCode,
           org.apache.axis.types.UnsignedInt batchId,
           String batchNumber,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseTransactionTypeCVV2Check CVV2Result,
           String currency,
           String id,
           String terminal) {
           this.account = account;
           this.amount = amount;
           this.authenticationCode = authenticationCode;
           this.batchId = batchId;
           this.batchNumber = batchNumber;
           this.CVV2Result = CVV2Result;
           this.currency = currency;
           this.id = id;
           this.terminal = terminal;
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
     * Gets the account value for this ResponseTransactionType.
     *
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account value for this ResponseTransactionType.
     *
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Gets the amount value for this ResponseTransactionType.
     *
     * @return amount
     */
    public org.apache.axis.types.UnsignedInt getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this ResponseTransactionType.
     *
     * @param amount
     */
    public void setAmount(org.apache.axis.types.UnsignedInt amount) {
        this.amount = amount;
    }

    /**
     * Gets the authenticationCode value for this ResponseTransactionType.
     *
     * @return authenticationCode
     */
    public String getAuthenticationCode() {
        return authenticationCode;
    }

    /**
     * Sets the authenticationCode value for this ResponseTransactionType.
     *
     * @param authenticationCode
     */
    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    /**
     * Gets the batchId value for this ResponseTransactionType.
     *
     * @return batchId
     */
    public org.apache.axis.types.UnsignedInt getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId value for this ResponseTransactionType.
     *
     * @param batchId
     */
    public void setBatchId(org.apache.axis.types.UnsignedInt batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the batchNumber value for this ResponseTransactionType.
     *
     * @return batchNumber
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * Sets the batchNumber value for this ResponseTransactionType.
     *
     * @param batchNumber
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * Gets the CVV2Result value for this ResponseTransactionType.
     *
     * @return CVV2Result
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseTransactionTypeCVV2Check getCVV2Result() {
        return CVV2Result;
    }

    /**
     * Sets the CVV2Result value for this ResponseTransactionType.
     *
     * @param CVV2Result
     */
    public void setCVV2Result(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResponseTransactionTypeCVV2Check CVV2Result) {
        this.CVV2Result = CVV2Result;
    }

    /**
     * Gets the currency value for this ResponseTransactionType.
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency value for this ResponseTransactionType.
     *
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the id value for this ResponseTransactionType.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id value for this ResponseTransactionType.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the terminal value for this ResponseTransactionType.
     *
     * @return terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal value for this ResponseTransactionType.
     *
     * @param terminal
     */
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ResponseTransactionType)) return false;
        ResponseTransactionType other = (ResponseTransactionType) obj;
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
            ((this.authenticationCode==null && other.getAuthenticationCode()==null) ||
             (this.authenticationCode!=null &&
              this.authenticationCode.equals(other.getAuthenticationCode()))) &&
            ((this.batchId==null && other.getBatchId()==null) ||
             (this.batchId!=null &&
              this.batchId.equals(other.getBatchId()))) &&
            ((this.batchNumber==null && other.getBatchNumber()==null) ||
             (this.batchNumber!=null &&
              this.batchNumber.equals(other.getBatchNumber()))) &&
            ((this.CVV2Result==null && other.getCVV2Result()==null) ||
             (this.CVV2Result!=null &&
              this.CVV2Result.equals(other.getCVV2Result()))) &&
            ((this.currency==null && other.getCurrency()==null) ||
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.id==null && other.getId()==null) ||
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.terminal==null && other.getTerminal()==null) ||
             (this.terminal!=null &&
              this.terminal.equals(other.getTerminal())));
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
        if (getAuthenticationCode() != null) {
            _hashCode += getAuthenticationCode().hashCode();
        }
        if (getBatchId() != null) {
            _hashCode += getBatchId().hashCode();
        }
        if (getBatchNumber() != null) {
            _hashCode += getBatchNumber().hashCode();
        }
        if (getCVV2Result() != null) {
            _hashCode += getCVV2Result().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getTerminal() != null) {
            _hashCode += getTerminal().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

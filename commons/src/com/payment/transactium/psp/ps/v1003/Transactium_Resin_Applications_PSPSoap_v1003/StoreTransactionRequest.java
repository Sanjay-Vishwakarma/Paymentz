/**
 * StoreTransactionRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class StoreTransactionRequest  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StoreTransactionRequest.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "StoreTransactionRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ARN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ARN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "AccountID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authentication");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Authentication"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.AuthenticationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BillingEmail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("financialSign");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "FinancialSign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "SignType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("references");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "References"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.ReferencesType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ResultCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "TerminalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "TerminalReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String ARN;
    private Long accountID;
    private org.apache.axis.types.UnsignedInt amount;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication;
    private String billingEmail;
    private String currency;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.SignType financialSign;
    private String hostCode;
    private java.util.Calendar hostDate;
    private String hostMessage;
    private String hostReference;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode status;
    private Long terminalID;
    private String terminalReference;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public StoreTransactionRequest() {
    }


    public StoreTransactionRequest(
           String ARN,
           Long accountID,
           org.apache.axis.types.UnsignedInt amount,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication,
           String billingEmail,
           String currency,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.SignType financialSign,
           String hostCode,
           java.util.Calendar hostDate,
           String hostMessage,
           String hostReference,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode status,
           Long terminalID,
           String terminalReference) {
           this.ARN = ARN;
           this.accountID = accountID;
           this.amount = amount;
           this.authentication = authentication;
           this.billingEmail = billingEmail;
           this.currency = currency;
           this.financialSign = financialSign;
           this.hostCode = hostCode;
           this.hostDate = hostDate;
           this.hostMessage = hostMessage;
           this.hostReference = hostReference;
           this.references = references;
           this.status = status;
           this.terminalID = terminalID;
           this.terminalReference = terminalReference;
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
     * Gets the ARN value for this StoreTransactionRequest.
     *
     * @return ARN
     */
    public String getARN() {
        return ARN;
    }

    /**
     * Sets the ARN value for this StoreTransactionRequest.
     *
     * @param ARN
     */
    public void setARN(String ARN) {
        this.ARN = ARN;
    }

    /**
     * Gets the accountID value for this StoreTransactionRequest.
     *
     * @return accountID
     */
    public Long getAccountID() {
        return accountID;
    }

    /**
     * Sets the accountID value for this StoreTransactionRequest.
     *
     * @param accountID
     */
    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    }

    /**
     * Gets the amount value for this StoreTransactionRequest.
     *
     * @return amount
     */
    public org.apache.axis.types.UnsignedInt getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this StoreTransactionRequest.
     *
     * @param amount
     */
    public void setAmount(org.apache.axis.types.UnsignedInt amount) {
        this.amount = amount;
    }

    /**
     * Gets the authentication value for this StoreTransactionRequest.
     *
     * @return authentication
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType getAuthentication() {
        return authentication;
    }

    /**
     * Sets the authentication value for this StoreTransactionRequest.
     *
     * @param authentication
     */
    public void setAuthentication(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication) {
        this.authentication = authentication;
    }

    /**
     * Gets the billingEmail value for this StoreTransactionRequest.
     *
     * @return billingEmail
     */
    public String getBillingEmail() {
        return billingEmail;
    }

    /**
     * Sets the billingEmail value for this StoreTransactionRequest.
     *
     * @param billingEmail
     */
    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    /**
     * Gets the currency value for this StoreTransactionRequest.
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency value for this StoreTransactionRequest.
     *
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the financialSign value for this StoreTransactionRequest.
     *
     * @return financialSign
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.SignType getFinancialSign() {
        return financialSign;
    }

    /**
     * Sets the financialSign value for this StoreTransactionRequest.
     *
     * @param financialSign
     */
    public void setFinancialSign(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.SignType financialSign) {
        this.financialSign = financialSign;
    }

    /**
     * Gets the hostCode value for this StoreTransactionRequest.
     *
     * @return hostCode
     */
    public String getHostCode() {
        return hostCode;
    }

    /**
     * Sets the hostCode value for this StoreTransactionRequest.
     *
     * @param hostCode
     */
    public void setHostCode(String hostCode) {
        this.hostCode = hostCode;
    }

    /**
     * Gets the hostDate value for this StoreTransactionRequest.
     *
     * @return hostDate
     */
    public java.util.Calendar getHostDate() {
        return hostDate;
    }

    /**
     * Sets the hostDate value for this StoreTransactionRequest.
     *
     * @param hostDate
     */
    public void setHostDate(java.util.Calendar hostDate) {
        this.hostDate = hostDate;
    }

    /**
     * Gets the hostMessage value for this StoreTransactionRequest.
     *
     * @return hostMessage
     */
    public String getHostMessage() {
        return hostMessage;
    }

    /**
     * Sets the hostMessage value for this StoreTransactionRequest.
     *
     * @param hostMessage
     */
    public void setHostMessage(String hostMessage) {
        this.hostMessage = hostMessage;
    }

    /**
     * Gets the hostReference value for this StoreTransactionRequest.
     *
     * @return hostReference
     */
    public String getHostReference() {
        return hostReference;
    }

    /**
     * Sets the hostReference value for this StoreTransactionRequest.
     *
     * @param hostReference
     */
    public void setHostReference(String hostReference) {
        this.hostReference = hostReference;
    }

    /**
     * Gets the references value for this StoreTransactionRequest.
     *
     * @return references
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType getReferences() {
        return references;
    }

    /**
     * Sets the references value for this StoreTransactionRequest.
     *
     * @param references
     */
    public void setReferences(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references) {
        this.references = references;
    }

    /**
     * Gets the status value for this StoreTransactionRequest.
     *
     * @return status
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode getStatus() {
        return status;
    }

    /**
     * Sets the status value for this StoreTransactionRequest.
     *
     * @param status
     */
    public void setStatus(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode status) {
        this.status = status;
    }

    /**
     * Gets the terminalID value for this StoreTransactionRequest.
     *
     * @return terminalID
     */
    public Long getTerminalID() {
        return terminalID;
    }

    /**
     * Sets the terminalID value for this StoreTransactionRequest.
     *
     * @param terminalID
     */
    public void setTerminalID(Long terminalID) {
        this.terminalID = terminalID;
    }

    /**
     * Gets the terminalReference value for this StoreTransactionRequest.
     *
     * @return terminalReference
     */
    public String getTerminalReference() {
        return terminalReference;
    }

    /**
     * Sets the terminalReference value for this StoreTransactionRequest.
     *
     * @param terminalReference
     */
    public void setTerminalReference(String terminalReference) {
        this.terminalReference = terminalReference;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof StoreTransactionRequest)) return false;
        StoreTransactionRequest other = (StoreTransactionRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.ARN==null && other.getARN()==null) ||
             (this.ARN!=null &&
              this.ARN.equals(other.getARN()))) &&
            ((this.accountID==null && other.getAccountID()==null) ||
             (this.accountID!=null &&
              this.accountID.equals(other.getAccountID()))) &&
            ((this.amount==null && other.getAmount()==null) ||
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.authentication==null && other.getAuthentication()==null) ||
             (this.authentication!=null &&
              this.authentication.equals(other.getAuthentication()))) &&
            ((this.billingEmail==null && other.getBillingEmail()==null) ||
             (this.billingEmail!=null &&
              this.billingEmail.equals(other.getBillingEmail()))) &&
            ((this.currency==null && other.getCurrency()==null) ||
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.financialSign==null && other.getFinancialSign()==null) ||
             (this.financialSign!=null &&
              this.financialSign.equals(other.getFinancialSign()))) &&
            ((this.hostCode==null && other.getHostCode()==null) ||
             (this.hostCode!=null &&
              this.hostCode.equals(other.getHostCode()))) &&
            ((this.hostDate==null && other.getHostDate()==null) ||
             (this.hostDate!=null &&
              this.hostDate.equals(other.getHostDate()))) &&
            ((this.hostMessage==null && other.getHostMessage()==null) ||
             (this.hostMessage!=null &&
              this.hostMessage.equals(other.getHostMessage()))) &&
            ((this.hostReference==null && other.getHostReference()==null) ||
             (this.hostReference!=null &&
              this.hostReference.equals(other.getHostReference()))) &&
            ((this.references==null && other.getReferences()==null) ||
             (this.references!=null &&
              this.references.equals(other.getReferences()))) &&
            ((this.status==null && other.getStatus()==null) ||
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.terminalID==null && other.getTerminalID()==null) ||
             (this.terminalID!=null &&
              this.terminalID.equals(other.getTerminalID()))) &&
            ((this.terminalReference==null && other.getTerminalReference()==null) ||
             (this.terminalReference!=null &&
              this.terminalReference.equals(other.getTerminalReference())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getARN() != null) {
            _hashCode += getARN().hashCode();
        }
        if (getAccountID() != null) {
            _hashCode += getAccountID().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getAuthentication() != null) {
            _hashCode += getAuthentication().hashCode();
        }
        if (getBillingEmail() != null) {
            _hashCode += getBillingEmail().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getFinancialSign() != null) {
            _hashCode += getFinancialSign().hashCode();
        }
        if (getHostCode() != null) {
            _hashCode += getHostCode().hashCode();
        }
        if (getHostDate() != null) {
            _hashCode += getHostDate().hashCode();
        }
        if (getHostMessage() != null) {
            _hashCode += getHostMessage().hashCode();
        }
        if (getHostReference() != null) {
            _hashCode += getHostReference().hashCode();
        }
        if (getReferences() != null) {
            _hashCode += getReferences().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getTerminalID() != null) {
            _hashCode += getTerminalID().hashCode();
        }
        if (getTerminalReference() != null) {
            _hashCode += getTerminalReference().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

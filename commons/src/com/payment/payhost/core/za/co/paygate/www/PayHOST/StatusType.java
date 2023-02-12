/**
 * StatusType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class StatusType  implements java.io.Serializable {
    private java.lang.String transactionId;

    private java.lang.String reference;

    private java.lang.String acquirerCode;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusNameType statusName;

    private org.apache.axis.types.Token statusDetail;

    private org.apache.axis.types.Token authCode;

    private org.apache.axis.types.Token payRequestId;

    //private org.apache.axis.types.Token vaultId;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType[] payVaultData;

    private org.apache.axis.types.Token transactionStatusCode;

    private org.apache.axis.types.Token transactionStatusDescription;

    private org.apache.axis.types.Token resultCode;

    private org.apache.axis.types.Token resultDescription;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency;

    private java.lang.Integer amount;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType requestedCurrency;

    private java.lang.Integer requestedAmount;

    private org.apache.axis.types.Token conversionRate;

    private org.apache.axis.types.Token riskIndicator;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentType paymentType;

    private org.apache.axis.types.Token billingDescriptor;

    private java.util.Calendar dateTime;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.TransactionType transactionType;

    public StatusType() {
    }

    public StatusType(
           java.lang.String transactionId,
           java.lang.String reference,
           java.lang.String acquirerCode,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusNameType statusName,
           org.apache.axis.types.Token statusDetail,
           org.apache.axis.types.Token authCode,
           org.apache.axis.types.Token payRequestId,
           //org.apache.axis.types.Token vaultId,
           //com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType[] payVaultData,
           org.apache.axis.types.Token transactionStatusCode,
           org.apache.axis.types.Token transactionStatusDescription,
           org.apache.axis.types.Token resultCode,
           org.apache.axis.types.Token resultDescription,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency,
           java.lang.Integer amount,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType requestedCurrency,
           java.lang.Integer requestedAmount,
           org.apache.axis.types.Token conversionRate,
           org.apache.axis.types.Token riskIndicator,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentType paymentType,
           org.apache.axis.types.Token billingDescriptor,
           java.util.Calendar dateTime)
           //com.payment.payhost.core.za.co.paygate.www.PayHOST.TransactionType transactionType)
    {
           this.transactionId = transactionId;
           this.reference = reference;
           this.acquirerCode = acquirerCode;
           this.statusName = statusName;
           this.statusDetail = statusDetail;
           this.authCode = authCode;
           this.payRequestId = payRequestId;
           //this.vaultId = vaultId;
           //this.payVaultData = payVaultData;
           this.transactionStatusCode = transactionStatusCode;
           this.transactionStatusDescription = transactionStatusDescription;
           this.resultCode = resultCode;
           this.resultDescription = resultDescription;
           this.currency = currency;
           this.amount = amount;
           this.requestedCurrency = requestedCurrency;
           this.requestedAmount = requestedAmount;
           this.conversionRate = conversionRate;
           this.riskIndicator = riskIndicator;
           this.paymentType = paymentType;
           this.billingDescriptor = billingDescriptor;
           this.dateTime = dateTime;
           //this.transactionType = transactionType;
    }


    /**
     * Gets the transactionId value for this StatusType.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this StatusType.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the reference value for this StatusType.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this StatusType.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the acquirerCode value for this StatusType.
     * 
     * @return acquirerCode
     */
    public java.lang.String getAcquirerCode() {
        return acquirerCode;
    }


    /**
     * Sets the acquirerCode value for this StatusType.
     * 
     * @param acquirerCode
     */
    public void setAcquirerCode(java.lang.String acquirerCode) {
        this.acquirerCode = acquirerCode;
    }


    /**
     * Gets the statusName value for this StatusType.
     * 
     * @return statusName
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusNameType getStatusName() {
        return statusName;
    }

    /**
     * Sets the statusName value for this StatusType.
     * 
     * @param statusName
     */
    public void setStatusName(com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusNameType statusName) {
        this.statusName = statusName;
    }

    /**
     * Gets the statusDetail value for this StatusType.
     * 
     * @return statusDetail
     */
    public org.apache.axis.types.Token getStatusDetail() {
        return statusDetail;
    }


    /**
     * Sets the statusDetail value for this StatusType.
     * 
     * @param statusDetail
     */
    public void setStatusDetail(org.apache.axis.types.Token statusDetail) {
        this.statusDetail = statusDetail;
    }


    /**
     * Gets the authCode value for this StatusType.
     * 
     * @return authCode
     */
    public org.apache.axis.types.Token getAuthCode() {
        return authCode;
    }


    /**
     * Sets the authCode value for this StatusType.
     * 
     * @param authCode
     */
    public void setAuthCode(org.apache.axis.types.Token authCode) {
        this.authCode = authCode;
    }


    /**
     * Gets the payRequestId value for this StatusType.
     * 
     * @return payRequestId
     */
    public org.apache.axis.types.Token getPayRequestId() {
        return payRequestId;
    }


    /**
     * Sets the payRequestId value for this StatusType.
     * 
     * @param payRequestId
     */
    public void setPayRequestId(org.apache.axis.types.Token payRequestId) {
        this.payRequestId = payRequestId;
    }


    /**
     * Gets the vaultId value for this StatusType.
     * 
     * @return vaultId
     */
    /*public org.apache.axis.types.Token getVaultId() {
        return vaultId;
    }*/


    /**
     * Sets the vaultId value for this StatusType.
     * 
     * @param vaultId
     */
    /*public void setVaultId(org.apache.axis.types.Token vaultId) {
        this.vaultId = vaultId;
    }*/


    /**
     * Gets the payVaultData value for this StatusType.
     * 
     * @return payVaultData
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType[] getPayVaultData() {
        return payVaultData;
    }
*/

    /**
     * Gets the transactionStatusCode value for this StatusType.
     *
     * @return transactionStatusCode
     */
    public org.apache.axis.types.Token getTransactionStatusCode() {
        return transactionStatusCode;
    }


    /**
     * Sets the transactionStatusCode value for this StatusType.
     *
     * @param transactionStatusCode
     */
    public void setTransactionStatusCode(org.apache.axis.types.Token transactionStatusCode) {
        this.transactionStatusCode = transactionStatusCode;
    }


    /*public void setPayVaultData(com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType[] payVaultData) {
        this.payVaultData = payVaultData;
    }
*/
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType getPayVaultData(int i) {
        return this.payVaultData[i];
    }
*/
    /*public void setPayVaultData(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType _value) {
        this.payVaultData[i] = _value;
    }
*/

    /**
     * Gets the transactionStatusDescription value for this StatusType.
     *
     * @return transactionStatusDescription
     */
    public org.apache.axis.types.Token getTransactionStatusDescription() {
        return transactionStatusDescription;
    }


    /**
     * Sets the transactionStatusDescription value for this StatusType.
     * 
     * @param transactionStatusDescription
     */
    public void setTransactionStatusDescription(org.apache.axis.types.Token transactionStatusDescription) {
        this.transactionStatusDescription = transactionStatusDescription;
    }


    /**
     * Gets the resultCode value for this StatusType.
     * 
     * @return resultCode
     */
    public org.apache.axis.types.Token getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this StatusType.
     * 
     * @param resultCode
     */
    public void setResultCode(org.apache.axis.types.Token resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the resultDescription value for this StatusType.
     * 
     * @return resultDescription
     */
    public org.apache.axis.types.Token getResultDescription() {
        return resultDescription;
    }


    /**
     * Sets the resultDescription value for this StatusType.
     * 
     * @param resultDescription
     */
    public void setResultDescription(org.apache.axis.types.Token resultDescription) {
        this.resultDescription = resultDescription;
    }


    /**
     * Gets the currency value for this StatusType.
     * 
     * @return currency
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this StatusType.
     * 
     * @param currency
     */
    public void setCurrency(com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency) {
        this.currency = currency;
    }


    /**
     * Gets the amount value for this StatusType.
     * 
     * @return amount
     */
    public java.lang.Integer getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this StatusType.
     * 
     * @param amount
     */
    public void setAmount(java.lang.Integer amount) {
        this.amount = amount;
    }


    /**
     * Gets the requestedCurrency value for this StatusType.
     * 
     * @return requestedCurrency
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType getRequestedCurrency() {
        return requestedCurrency;
    }


    /**
     * Sets the requestedCurrency value for this StatusType.
     * 
     * @param requestedCurrency
     */
    public void setRequestedCurrency(com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType requestedCurrency) {
        this.requestedCurrency = requestedCurrency;
    }


    /**
     * Gets the requestedAmount value for this StatusType.
     * 
     * @return requestedAmount
     */
    public java.lang.Integer getRequestedAmount() {
        return requestedAmount;
    }


    /**
     * Sets the requestedAmount value for this StatusType.
     * 
     * @param requestedAmount
     */
    public void setRequestedAmount(java.lang.Integer requestedAmount) {
        this.requestedAmount = requestedAmount;
    }


    /**
     * Gets the conversionRate value for this StatusType.
     * 
     * @return conversionRate
     */
    public org.apache.axis.types.Token getConversionRate() {
        return conversionRate;
    }


    /**
     * Sets the conversionRate value for this StatusType.
     * 
     * @param conversionRate
     */
    public void setConversionRate(org.apache.axis.types.Token conversionRate) {
        this.conversionRate = conversionRate;
    }


    /**
     * Gets the riskIndicator value for this StatusType.
     * 
     * @return riskIndicator
     */
    public org.apache.axis.types.Token getRiskIndicator() {
        return riskIndicator;
    }


    /**
     * Sets the riskIndicator value for this StatusType.
     * 
     * @param riskIndicator
     */
    public void setRiskIndicator(org.apache.axis.types.Token riskIndicator) {
        this.riskIndicator = riskIndicator;
    }


    /**
     * Gets the paymentType value for this StatusType.
     * 
     * @return paymentType
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentType getPaymentType() {
        return paymentType;
    }


    /**
     * Sets the paymentType value for this StatusType.
     * 
     * @param paymentType
     */
    public void setPaymentType(com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentType paymentType) {
        this.paymentType = paymentType;
    }


    /**
     * Gets the billingDescriptor value for this StatusType.
     * 
     * @return billingDescriptor
     */
    public org.apache.axis.types.Token getBillingDescriptor() {
        return billingDescriptor;
    }


    /**
     * Sets the billingDescriptor value for this StatusType.
     * 
     * @param billingDescriptor
     */
    public void setBillingDescriptor(org.apache.axis.types.Token billingDescriptor) {
        this.billingDescriptor = billingDescriptor;
    }


    /**
     * Gets the dateTime value for this StatusType.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this StatusType.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the transactionType value for this StatusType.
     * 
     * @return transactionType
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.TransactionType getTransactionType() {
        return transactionType;
    }
*/

    /**
     * Sets the transactionType value for this StatusType.
     * 
     * @param transactionType
     */
    /*public void setTransactionType(com.payment.payhost.core.za.co.paygate.www.PayHOST.TransactionType transactionType) {
        this.transactionType = transactionType;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StatusType)) return false;
        StatusType other = (StatusType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.acquirerCode==null && other.getAcquirerCode()==null) || 
             (this.acquirerCode!=null &&
              this.acquirerCode.equals(other.getAcquirerCode()))) &&
            ((this.statusName==null && other.getStatusName()==null) || 
             (this.statusName!=null &&
              this.statusName.equals(other.getStatusName()))) &&
            ((this.statusDetail==null && other.getStatusDetail()==null) || 
             (this.statusDetail!=null &&
              this.statusDetail.equals(other.getStatusDetail()))) &&
            ((this.authCode==null && other.getAuthCode()==null) || 
             (this.authCode!=null &&
              this.authCode.equals(other.getAuthCode()))) &&
            ((this.payRequestId==null && other.getPayRequestId()==null) || 
             (this.payRequestId!=null &&
              this.payRequestId.equals(other.getPayRequestId()))) &&
            /*((this.vaultId==null && other.getVaultId()==null) ||
             (this.vaultId!=null &&
              this.vaultId.equals(other.getVaultId()))) &&
            ((this.payVaultData==null && other.getPayVaultData()==null) || 
             (this.payVaultData!=null &&
              java.util.Arrays.equals(this.payVaultData, other.getPayVaultData()))) &&*/
            ((this.transactionStatusCode==null && other.getTransactionStatusCode()==null) || 
             (this.transactionStatusCode!=null &&
              this.transactionStatusCode.equals(other.getTransactionStatusCode()))) &&
            ((this.transactionStatusDescription==null && other.getTransactionStatusDescription()==null) || 
             (this.transactionStatusDescription!=null &&
              this.transactionStatusDescription.equals(other.getTransactionStatusDescription()))) &&
            ((this.resultCode==null && other.getResultCode()==null) || 
             (this.resultCode!=null &&
              this.resultCode.equals(other.getResultCode()))) &&
            ((this.resultDescription==null && other.getResultDescription()==null) || 
             (this.resultDescription!=null &&
              this.resultDescription.equals(other.getResultDescription()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.requestedCurrency==null && other.getRequestedCurrency()==null) || 
             (this.requestedCurrency!=null &&
              this.requestedCurrency.equals(other.getRequestedCurrency()))) &&
            ((this.requestedAmount==null && other.getRequestedAmount()==null) || 
             (this.requestedAmount!=null &&
              this.requestedAmount.equals(other.getRequestedAmount()))) &&
            ((this.conversionRate==null && other.getConversionRate()==null) || 
             (this.conversionRate!=null &&
              this.conversionRate.equals(other.getConversionRate()))) &&
            ((this.riskIndicator==null && other.getRiskIndicator()==null) || 
             (this.riskIndicator!=null &&
              this.riskIndicator.equals(other.getRiskIndicator()))) &&
            ((this.paymentType==null && other.getPaymentType()==null) || 
             (this.paymentType!=null &&
              this.paymentType.equals(other.getPaymentType()))) &&
            ((this.billingDescriptor==null && other.getBillingDescriptor()==null) || 
             (this.billingDescriptor!=null &&
              this.billingDescriptor.equals(other.getBillingDescriptor()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) /*&&
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType())))*/;
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
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getAcquirerCode() != null) {
            _hashCode += getAcquirerCode().hashCode();
        }
        if (getStatusName() != null)
        {
            _hashCode += getStatusName().hashCode();
        }
        if (getStatusDetail() != null) {
            _hashCode += getStatusDetail().hashCode();
        }
        if (getAuthCode() != null) {
            _hashCode += getAuthCode().hashCode();
        }
        if (getPayRequestId() != null) {
            _hashCode += getPayRequestId().hashCode();
        }
        /*if (getVaultId() != null) {
            _hashCode += getVaultId().hashCode();
        }*/
        /*if (getPayVaultData() != null)
        {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPayVaultData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPayVaultData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }*/
        if (getTransactionStatusCode() != null) {
            _hashCode += getTransactionStatusCode().hashCode();
        }
        if (getTransactionStatusDescription() != null) {
            _hashCode += getTransactionStatusDescription().hashCode();
        }
        if (getResultCode() != null) {
            _hashCode += getResultCode().hashCode();
        }
        if (getResultDescription() != null) {
            _hashCode += getResultDescription().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getRequestedCurrency() != null) {
            _hashCode += getRequestedCurrency().hashCode();
        }
        if (getRequestedAmount() != null) {
            _hashCode += getRequestedAmount().hashCode();
        }
        if (getConversionRate() != null) {
            _hashCode += getConversionRate().hashCode();
        }
        if (getRiskIndicator() != null) {
            _hashCode += getRiskIndicator().hashCode();
        }
        if (getPaymentType() != null) {
            _hashCode += getPaymentType().hashCode();
        }
        if (getBillingDescriptor() != null) {
            _hashCode += getBillingDescriptor().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        /*if (getTransactionType() != null)
        {
            _hashCode += getTransactionType().hashCode();
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StatusType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);

        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquirerCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AcquirerCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusNameType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AuthCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payRequestId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayRequestId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        /*elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vaultId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VaultId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);*/
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payVaultData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayVaultData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VaultDataType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionStatusCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatusDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionStatusDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ResultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ResultDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CurrencyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RequestedCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CurrencyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RequestedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conversionRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ConversionRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riskIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RiskIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BillingDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "DateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionType"));
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

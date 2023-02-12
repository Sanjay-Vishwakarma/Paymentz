/**
 * RedirectUrlRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService;

public class RedirectUrlRequest  implements java.io.Serializable {
    private com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant;

    private com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.SubMerchantCredentials subMerchant;

    private java.lang.String affiliateId;

    private java.lang.String merchantTnxId;

    private java.lang.String processType;

    private java.lang.String transactionAmount;

    private java.lang.String transactionCurrency;

    private java.lang.String transactionType;

    private java.lang.String secondaryAuthorization;

    private java.lang.String transactionDescription;

    private java.lang.String receiverUserid;

    private java.util.Date localDate;

    private java.lang.String cancelUrl;

    private java.lang.String returnlUrl;

    public RedirectUrlRequest() {
    }

    public RedirectUrlRequest(
           com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant,
           com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.SubMerchantCredentials subMerchant,
           java.lang.String affiliateId,
           java.lang.String merchantTnxId,
           java.lang.String processType,
           java.lang.String transactionAmount,
           java.lang.String transactionCurrency,
           java.lang.String transactionType,
           java.lang.String secondaryAuthorization,
           java.lang.String transactionDescription,
           java.lang.String receiverUserid,
           java.util.Date localDate,
           java.lang.String cancelUrl,
           java.lang.String returnlUrl) {
           this.merchant = merchant;
           this.subMerchant = subMerchant;
           this.affiliateId = affiliateId;
           this.merchantTnxId = merchantTnxId;
           this.processType = processType;
           this.transactionAmount = transactionAmount;
           this.transactionCurrency = transactionCurrency;
           this.transactionType = transactionType;
           this.secondaryAuthorization = secondaryAuthorization;
           this.transactionDescription = transactionDescription;
           this.receiverUserid = receiverUserid;
           this.localDate = localDate;
           this.cancelUrl = cancelUrl;
           this.returnlUrl = returnlUrl;
    }


    /**
     * Gets the merchant value for this RedirectUrlRequest.
     * 
     * @return merchant
     */
    public com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials getMerchant() {
        return merchant;
    }


    /**
     * Sets the merchant value for this RedirectUrlRequest.
     * 
     * @param merchant
     */
    public void setMerchant(com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant) {
        this.merchant = merchant;
    }


    /**
     * Gets the subMerchant value for this RedirectUrlRequest.
     * 
     * @return subMerchant
     */
    public com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.SubMerchantCredentials getSubMerchant() {
        return subMerchant;
    }


    /**
     * Sets the subMerchant value for this RedirectUrlRequest.
     * 
     * @param subMerchant
     */
    public void setSubMerchant(com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.SubMerchantCredentials subMerchant) {
        this.subMerchant = subMerchant;
    }


    /**
     * Gets the affiliateId value for this RedirectUrlRequest.
     * 
     * @return affiliateId
     */
    public java.lang.String getAffiliateId() {
        return affiliateId;
    }


    /**
     * Sets the affiliateId value for this RedirectUrlRequest.
     * 
     * @param affiliateId
     */
    public void setAffiliateId(java.lang.String affiliateId) {
        this.affiliateId = affiliateId;
    }


    /**
     * Gets the merchantTnxId value for this RedirectUrlRequest.
     * 
     * @return merchantTnxId
     */
    public java.lang.String getMerchantTnxId() {
        return merchantTnxId;
    }


    /**
     * Sets the merchantTnxId value for this RedirectUrlRequest.
     * 
     * @param merchantTnxId
     */
    public void setMerchantTnxId(java.lang.String merchantTnxId) {
        this.merchantTnxId = merchantTnxId;
    }


    /**
     * Gets the processType value for this RedirectUrlRequest.
     * 
     * @return processType
     */
    public java.lang.String getProcessType() {
        return processType;
    }


    /**
     * Sets the processType value for this RedirectUrlRequest.
     * 
     * @param processType
     */
    public void setProcessType(java.lang.String processType) {
        this.processType = processType;
    }


    /**
     * Gets the transactionAmount value for this RedirectUrlRequest.
     * 
     * @return transactionAmount
     */
    public java.lang.String getTransactionAmount() {
        return transactionAmount;
    }


    /**
     * Sets the transactionAmount value for this RedirectUrlRequest.
     * 
     * @param transactionAmount
     */
    public void setTransactionAmount(java.lang.String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    /**
     * Gets the transactionCurrency value for this RedirectUrlRequest.
     * 
     * @return transactionCurrency
     */
    public java.lang.String getTransactionCurrency() {
        return transactionCurrency;
    }


    /**
     * Sets the transactionCurrency value for this RedirectUrlRequest.
     * 
     * @param transactionCurrency
     */
    public void setTransactionCurrency(java.lang.String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }


    /**
     * Gets the transactionType value for this RedirectUrlRequest.
     * 
     * @return transactionType
     */
    public java.lang.String getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this RedirectUrlRequest.
     * 
     * @param transactionType
     */
    public void setTransactionType(java.lang.String transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the secondaryAuthorization value for this RedirectUrlRequest.
     * 
     * @return secondaryAuthorization
     */
    public java.lang.String getSecondaryAuthorization() {
        return secondaryAuthorization;
    }


    /**
     * Sets the secondaryAuthorization value for this RedirectUrlRequest.
     * 
     * @param secondaryAuthorization
     */
    public void setSecondaryAuthorization(java.lang.String secondaryAuthorization) {
        this.secondaryAuthorization = secondaryAuthorization;
    }


    /**
     * Gets the transactionDescription value for this RedirectUrlRequest.
     * 
     * @return transactionDescription
     */
    public java.lang.String getTransactionDescription() {
        return transactionDescription;
    }


    /**
     * Sets the transactionDescription value for this RedirectUrlRequest.
     * 
     * @param transactionDescription
     */
    public void setTransactionDescription(java.lang.String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }


    /**
     * Gets the receiverUserid value for this RedirectUrlRequest.
     * 
     * @return receiverUserid
     */
    public java.lang.String getReceiverUserid() {
        return receiverUserid;
    }


    /**
     * Sets the receiverUserid value for this RedirectUrlRequest.
     * 
     * @param receiverUserid
     */
    public void setReceiverUserid(java.lang.String receiverUserid) {
        this.receiverUserid = receiverUserid;
    }


    /**
     * Gets the localDate value for this RedirectUrlRequest.
     * 
     * @return localDate
     */
    public java.util.Date getLocalDate() {
        return localDate;
    }


    /**
     * Sets the localDate value for this RedirectUrlRequest.
     * 
     * @param localDate
     */
    public void setLocalDate(java.util.Date localDate) {
        this.localDate = localDate;
    }


    /**
     * Gets the cancelUrl value for this RedirectUrlRequest.
     * 
     * @return cancelUrl
     */
    public java.lang.String getCancelUrl() {
        return cancelUrl;
    }


    /**
     * Sets the cancelUrl value for this RedirectUrlRequest.
     * 
     * @param cancelUrl
     */
    public void setCancelUrl(java.lang.String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }


    /**
     * Gets the returnlUrl value for this RedirectUrlRequest.
     * 
     * @return returnlUrl
     */
    public java.lang.String getReturnlUrl() {
        return returnlUrl;
    }


    /**
     * Sets the returnlUrl value for this RedirectUrlRequest.
     * 
     * @param returnlUrl
     */
    public void setReturnlUrl(java.lang.String returnlUrl) {
        this.returnlUrl = returnlUrl;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RedirectUrlRequest)) return false;
        RedirectUrlRequest other = (RedirectUrlRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchant==null && other.getMerchant()==null) || 
             (this.merchant!=null &&
              this.merchant.equals(other.getMerchant()))) &&
            ((this.subMerchant==null && other.getSubMerchant()==null) || 
             (this.subMerchant!=null &&
              this.subMerchant.equals(other.getSubMerchant()))) &&
            ((this.affiliateId==null && other.getAffiliateId()==null) || 
             (this.affiliateId!=null &&
              this.affiliateId.equals(other.getAffiliateId()))) &&
            ((this.merchantTnxId==null && other.getMerchantTnxId()==null) || 
             (this.merchantTnxId!=null &&
              this.merchantTnxId.equals(other.getMerchantTnxId()))) &&
            ((this.processType==null && other.getProcessType()==null) || 
             (this.processType!=null &&
              this.processType.equals(other.getProcessType()))) &&
            ((this.transactionAmount==null && other.getTransactionAmount()==null) || 
             (this.transactionAmount!=null &&
              this.transactionAmount.equals(other.getTransactionAmount()))) &&
            ((this.transactionCurrency==null && other.getTransactionCurrency()==null) || 
             (this.transactionCurrency!=null &&
              this.transactionCurrency.equals(other.getTransactionCurrency()))) &&
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType()))) &&
            ((this.secondaryAuthorization==null && other.getSecondaryAuthorization()==null) || 
             (this.secondaryAuthorization!=null &&
              this.secondaryAuthorization.equals(other.getSecondaryAuthorization()))) &&
            ((this.transactionDescription==null && other.getTransactionDescription()==null) || 
             (this.transactionDescription!=null &&
              this.transactionDescription.equals(other.getTransactionDescription()))) &&
            ((this.receiverUserid==null && other.getReceiverUserid()==null) || 
             (this.receiverUserid!=null &&
              this.receiverUserid.equals(other.getReceiverUserid()))) &&
            ((this.localDate==null && other.getLocalDate()==null) || 
             (this.localDate!=null &&
              this.localDate.equals(other.getLocalDate()))) &&
            ((this.cancelUrl==null && other.getCancelUrl()==null) || 
             (this.cancelUrl!=null &&
              this.cancelUrl.equals(other.getCancelUrl()))) &&
            ((this.returnlUrl==null && other.getReturnlUrl()==null) || 
             (this.returnlUrl!=null &&
              this.returnlUrl.equals(other.getReturnlUrl())));
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
        if (getMerchant() != null) {
            _hashCode += getMerchant().hashCode();
        }
        if (getSubMerchant() != null) {
            _hashCode += getSubMerchant().hashCode();
        }
        if (getAffiliateId() != null) {
            _hashCode += getAffiliateId().hashCode();
        }
        if (getMerchantTnxId() != null) {
            _hashCode += getMerchantTnxId().hashCode();
        }
        if (getProcessType() != null) {
            _hashCode += getProcessType().hashCode();
        }
        if (getTransactionAmount() != null) {
            _hashCode += getTransactionAmount().hashCode();
        }
        if (getTransactionCurrency() != null) {
            _hashCode += getTransactionCurrency().hashCode();
        }
        if (getTransactionType() != null) {
            _hashCode += getTransactionType().hashCode();
        }
        if (getSecondaryAuthorization() != null) {
            _hashCode += getSecondaryAuthorization().hashCode();
        }
        if (getTransactionDescription() != null) {
            _hashCode += getTransactionDescription().hashCode();
        }
        if (getReceiverUserid() != null) {
            _hashCode += getReceiverUserid().hashCode();
        }
        if (getLocalDate() != null) {
            _hashCode += getLocalDate().hashCode();
        }
        if (getCancelUrl() != null) {
            _hashCode += getCancelUrl().hashCode();
        }
        if (getReturnlUrl() != null) {
            _hashCode += getReturnlUrl().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RedirectUrlRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", ">RedirectUrlRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "merchant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "MerchantCredentials"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subMerchant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "subMerchant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "SubMerchantCredentials"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("affiliateId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "affiliateId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantTnxId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "merchantTnxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "processType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryAuthorization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "secondaryAuthorization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiverUserid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "receiverUserid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "localDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cancelUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "cancelUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnlUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "returnlUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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

/**
 * TransactionData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class TransactionData  implements java.io.Serializable {
    private java.math.BigDecimal amount;

    private java.lang.String referenceNumber;

    private java.lang.String ticketNumber;

    private int MCSTransactionID;

    private int gatewayID;

    private java.lang.String thirdPartyMerchID;

    private java.lang.String thirdPartySubID;

    private java.lang.String third3RdPartyPin;

    private java.lang.String cardHolderName;

    private java.lang.String custom1;

    private java.lang.String custom2;

    private java.lang.String custom3;

    private java.lang.String custom4;

    private java.lang.String custom5;

    private java.lang.String custom6;

    private java.lang.String emailAddress;

    private java.lang.String countryCode;

    private java.lang.String currencyCode;

    private java.lang.String purchaseCardCustomerID;

    private java.math.BigDecimal purchaseCardTaxAmount;

    private java.lang.String purchaseCardTaxExempt;

    public TransactionData() {
    }

    public TransactionData(
           java.math.BigDecimal amount,
           java.lang.String referenceNumber,
           java.lang.String ticketNumber,
           int MCSTransactionID,
           int gatewayID,
           java.lang.String thirdPartyMerchID,
           java.lang.String thirdPartySubID,
           java.lang.String third3RdPartyPin,
           java.lang.String cardHolderName,
           java.lang.String custom1,
           java.lang.String custom2,
           java.lang.String custom3,
           java.lang.String custom4,
           java.lang.String custom5,
           java.lang.String custom6,
           java.lang.String emailAddress,
           java.lang.String countryCode,
           java.lang.String currencyCode,
           java.lang.String purchaseCardCustomerID,
           java.math.BigDecimal purchaseCardTaxAmount,
           java.lang.String purchaseCardTaxExempt) {
           this.amount = amount;
           this.referenceNumber = referenceNumber;
           this.ticketNumber = ticketNumber;
           this.MCSTransactionID = MCSTransactionID;
           this.gatewayID = gatewayID;
           this.thirdPartyMerchID = thirdPartyMerchID;
           this.thirdPartySubID = thirdPartySubID;
           this.third3RdPartyPin = third3RdPartyPin;
           this.cardHolderName = cardHolderName;
           this.custom1 = custom1;
           this.custom2 = custom2;
           this.custom3 = custom3;
           this.custom4 = custom4;
           this.custom5 = custom5;
           this.custom6 = custom6;
           this.emailAddress = emailAddress;
           this.countryCode = countryCode;
           this.currencyCode = currencyCode;
           this.purchaseCardCustomerID = purchaseCardCustomerID;
           this.purchaseCardTaxAmount = purchaseCardTaxAmount;
           this.purchaseCardTaxExempt = purchaseCardTaxExempt;
    }


    /**
     * Gets the amount value for this TransactionData.
     * 
     * @return amount
     */
    public java.math.BigDecimal getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this TransactionData.
     * 
     * @param amount
     */
    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }


    /**
     * Gets the referenceNumber value for this TransactionData.
     * 
     * @return referenceNumber
     */
    public java.lang.String getReferenceNumber() {
        return referenceNumber;
    }


    /**
     * Sets the referenceNumber value for this TransactionData.
     * 
     * @param referenceNumber
     */
    public void setReferenceNumber(java.lang.String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    /**
     * Gets the ticketNumber value for this TransactionData.
     * 
     * @return ticketNumber
     */
    public java.lang.String getTicketNumber() {
        return ticketNumber;
    }


    /**
     * Sets the ticketNumber value for this TransactionData.
     * 
     * @param ticketNumber
     */
    public void setTicketNumber(java.lang.String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    /**
     * Gets the MCSTransactionID value for this TransactionData.
     * 
     * @return MCSTransactionID
     */
    public int getMCSTransactionID() {
        return MCSTransactionID;
    }


    /**
     * Sets the MCSTransactionID value for this TransactionData.
     * 
     * @param MCSTransactionID
     */
    public void setMCSTransactionID(int MCSTransactionID) {
        this.MCSTransactionID = MCSTransactionID;
    }


    /**
     * Gets the gatewayID value for this TransactionData.
     * 
     * @return gatewayID
     */
    public int getGatewayID() {
        return gatewayID;
    }


    /**
     * Sets the gatewayID value for this TransactionData.
     * 
     * @param gatewayID
     */
    public void setGatewayID(int gatewayID) {
        this.gatewayID = gatewayID;
    }


    /**
     * Gets the thirdPartyMerchID value for this TransactionData.
     * 
     * @return thirdPartyMerchID
     */
    public java.lang.String getThirdPartyMerchID() {
        return thirdPartyMerchID;
    }


    /**
     * Sets the thirdPartyMerchID value for this TransactionData.
     * 
     * @param thirdPartyMerchID
     */
    public void setThirdPartyMerchID(java.lang.String thirdPartyMerchID) {
        this.thirdPartyMerchID = thirdPartyMerchID;
    }


    /**
     * Gets the thirdPartySubID value for this TransactionData.
     * 
     * @return thirdPartySubID
     */
    public java.lang.String getThirdPartySubID() {
        return thirdPartySubID;
    }


    /**
     * Sets the thirdPartySubID value for this TransactionData.
     * 
     * @param thirdPartySubID
     */
    public void setThirdPartySubID(java.lang.String thirdPartySubID) {
        this.thirdPartySubID = thirdPartySubID;
    }


    /**
     * Gets the third3RdPartyPin value for this TransactionData.
     * 
     * @return third3RdPartyPin
     */
    public java.lang.String getThird3RdPartyPin() {
        return third3RdPartyPin;
    }


    /**
     * Sets the third3RdPartyPin value for this TransactionData.
     * 
     * @param third3RdPartyPin
     */
    public void setThird3RdPartyPin(java.lang.String third3RdPartyPin) {
        this.third3RdPartyPin = third3RdPartyPin;
    }


    /**
     * Gets the cardHolderName value for this TransactionData.
     * 
     * @return cardHolderName
     */
    public java.lang.String getCardHolderName() {
        return cardHolderName;
    }


    /**
     * Sets the cardHolderName value for this TransactionData.
     * 
     * @param cardHolderName
     */
    public void setCardHolderName(java.lang.String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the custom1 value for this TransactionData.
     * 
     * @return custom1
     */
    public java.lang.String getCustom1() {
        return custom1;
    }


    /**
     * Sets the custom1 value for this TransactionData.
     * 
     * @param custom1
     */
    public void setCustom1(java.lang.String custom1) {
        this.custom1 = custom1;
    }


    /**
     * Gets the custom2 value for this TransactionData.
     * 
     * @return custom2
     */
    public java.lang.String getCustom2() {
        return custom2;
    }


    /**
     * Sets the custom2 value for this TransactionData.
     * 
     * @param custom2
     */
    public void setCustom2(java.lang.String custom2) {
        this.custom2 = custom2;
    }


    /**
     * Gets the custom3 value for this TransactionData.
     * 
     * @return custom3
     */
    public java.lang.String getCustom3() {
        return custom3;
    }


    /**
     * Sets the custom3 value for this TransactionData.
     * 
     * @param custom3
     */
    public void setCustom3(java.lang.String custom3) {
        this.custom3 = custom3;
    }


    /**
     * Gets the custom4 value for this TransactionData.
     * 
     * @return custom4
     */
    public java.lang.String getCustom4() {
        return custom4;
    }


    /**
     * Sets the custom4 value for this TransactionData.
     * 
     * @param custom4
     */
    public void setCustom4(java.lang.String custom4) {
        this.custom4 = custom4;
    }


    /**
     * Gets the custom5 value for this TransactionData.
     * 
     * @return custom5
     */
    public java.lang.String getCustom5() {
        return custom5;
    }


    /**
     * Sets the custom5 value for this TransactionData.
     * 
     * @param custom5
     */
    public void setCustom5(java.lang.String custom5) {
        this.custom5 = custom5;
    }


    /**
     * Gets the custom6 value for this TransactionData.
     * 
     * @return custom6
     */
    public java.lang.String getCustom6() {
        return custom6;
    }


    /**
     * Sets the custom6 value for this TransactionData.
     * 
     * @param custom6
     */
    public void setCustom6(java.lang.String custom6) {
        this.custom6 = custom6;
    }


    /**
     * Gets the emailAddress value for this TransactionData.
     * 
     * @return emailAddress
     */
    public java.lang.String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress value for this TransactionData.
     * 
     * @param emailAddress
     */
    public void setEmailAddress(java.lang.String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the countryCode value for this TransactionData.
     * 
     * @return countryCode
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }


    /**
     * Sets the countryCode value for this TransactionData.
     * 
     * @param countryCode
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the currencyCode value for this TransactionData.
     * 
     * @return currencyCode
     */
    public java.lang.String getCurrencyCode() {
        return currencyCode;
    }


    /**
     * Sets the currencyCode value for this TransactionData.
     * 
     * @param currencyCode
     */
    public void setCurrencyCode(java.lang.String currencyCode) {
        this.currencyCode = currencyCode;
    }


    /**
     * Gets the purchaseCardCustomerID value for this TransactionData.
     * 
     * @return purchaseCardCustomerID
     */
    public java.lang.String getPurchaseCardCustomerID() {
        return purchaseCardCustomerID;
    }


    /**
     * Sets the purchaseCardCustomerID value for this TransactionData.
     * 
     * @param purchaseCardCustomerID
     */
    public void setPurchaseCardCustomerID(java.lang.String purchaseCardCustomerID) {
        this.purchaseCardCustomerID = purchaseCardCustomerID;
    }


    /**
     * Gets the purchaseCardTaxAmount value for this TransactionData.
     * 
     * @return purchaseCardTaxAmount
     */
    public java.math.BigDecimal getPurchaseCardTaxAmount() {
        return purchaseCardTaxAmount;
    }


    /**
     * Sets the purchaseCardTaxAmount value for this TransactionData.
     * 
     * @param purchaseCardTaxAmount
     */
    public void setPurchaseCardTaxAmount(java.math.BigDecimal purchaseCardTaxAmount) {
        this.purchaseCardTaxAmount = purchaseCardTaxAmount;
    }


    /**
     * Gets the purchaseCardTaxExempt value for this TransactionData.
     * 
     * @return purchaseCardTaxExempt
     */
    public java.lang.String getPurchaseCardTaxExempt() {
        return purchaseCardTaxExempt;
    }


    /**
     * Sets the purchaseCardTaxExempt value for this TransactionData.
     * 
     * @param purchaseCardTaxExempt
     */
    public void setPurchaseCardTaxExempt(java.lang.String purchaseCardTaxExempt) {
        this.purchaseCardTaxExempt = purchaseCardTaxExempt;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionData)) return false;
        TransactionData other = (TransactionData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.referenceNumber==null && other.getReferenceNumber()==null) || 
             (this.referenceNumber!=null &&
              this.referenceNumber.equals(other.getReferenceNumber()))) &&
            ((this.ticketNumber==null && other.getTicketNumber()==null) || 
             (this.ticketNumber!=null &&
              this.ticketNumber.equals(other.getTicketNumber()))) &&
            this.MCSTransactionID == other.getMCSTransactionID() &&
            this.gatewayID == other.getGatewayID() &&
            ((this.thirdPartyMerchID==null && other.getThirdPartyMerchID()==null) || 
             (this.thirdPartyMerchID!=null &&
              this.thirdPartyMerchID.equals(other.getThirdPartyMerchID()))) &&
            ((this.thirdPartySubID==null && other.getThirdPartySubID()==null) || 
             (this.thirdPartySubID!=null &&
              this.thirdPartySubID.equals(other.getThirdPartySubID()))) &&
            ((this.third3RdPartyPin==null && other.getThird3RdPartyPin()==null) || 
             (this.third3RdPartyPin!=null &&
              this.third3RdPartyPin.equals(other.getThird3RdPartyPin()))) &&
            ((this.cardHolderName==null && other.getCardHolderName()==null) || 
             (this.cardHolderName!=null &&
              this.cardHolderName.equals(other.getCardHolderName()))) &&
            ((this.custom1==null && other.getCustom1()==null) || 
             (this.custom1!=null &&
              this.custom1.equals(other.getCustom1()))) &&
            ((this.custom2==null && other.getCustom2()==null) || 
             (this.custom2!=null &&
              this.custom2.equals(other.getCustom2()))) &&
            ((this.custom3==null && other.getCustom3()==null) || 
             (this.custom3!=null &&
              this.custom3.equals(other.getCustom3()))) &&
            ((this.custom4==null && other.getCustom4()==null) || 
             (this.custom4!=null &&
              this.custom4.equals(other.getCustom4()))) &&
            ((this.custom5==null && other.getCustom5()==null) || 
             (this.custom5!=null &&
              this.custom5.equals(other.getCustom5()))) &&
            ((this.custom6==null && other.getCustom6()==null) || 
             (this.custom6!=null &&
              this.custom6.equals(other.getCustom6()))) &&
            ((this.emailAddress==null && other.getEmailAddress()==null) || 
             (this.emailAddress!=null &&
              this.emailAddress.equals(other.getEmailAddress()))) &&
            ((this.countryCode==null && other.getCountryCode()==null) || 
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.currencyCode==null && other.getCurrencyCode()==null) || 
             (this.currencyCode!=null &&
              this.currencyCode.equals(other.getCurrencyCode()))) &&
            ((this.purchaseCardCustomerID==null && other.getPurchaseCardCustomerID()==null) || 
             (this.purchaseCardCustomerID!=null &&
              this.purchaseCardCustomerID.equals(other.getPurchaseCardCustomerID()))) &&
            ((this.purchaseCardTaxAmount==null && other.getPurchaseCardTaxAmount()==null) || 
             (this.purchaseCardTaxAmount!=null &&
              this.purchaseCardTaxAmount.equals(other.getPurchaseCardTaxAmount()))) &&
            ((this.purchaseCardTaxExempt==null && other.getPurchaseCardTaxExempt()==null) || 
             (this.purchaseCardTaxExempt!=null &&
              this.purchaseCardTaxExempt.equals(other.getPurchaseCardTaxExempt())));
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
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getReferenceNumber() != null) {
            _hashCode += getReferenceNumber().hashCode();
        }
        if (getTicketNumber() != null) {
            _hashCode += getTicketNumber().hashCode();
        }
        _hashCode += getMCSTransactionID();
        _hashCode += getGatewayID();
        if (getThirdPartyMerchID() != null) {
            _hashCode += getThirdPartyMerchID().hashCode();
        }
        if (getThirdPartySubID() != null) {
            _hashCode += getThirdPartySubID().hashCode();
        }
        if (getThird3RdPartyPin() != null) {
            _hashCode += getThird3RdPartyPin().hashCode();
        }
        if (getCardHolderName() != null) {
            _hashCode += getCardHolderName().hashCode();
        }
        if (getCustom1() != null) {
            _hashCode += getCustom1().hashCode();
        }
        if (getCustom2() != null) {
            _hashCode += getCustom2().hashCode();
        }
        if (getCustom3() != null) {
            _hashCode += getCustom3().hashCode();
        }
        if (getCustom4() != null) {
            _hashCode += getCustom4().hashCode();
        }
        if (getCustom5() != null) {
            _hashCode += getCustom5().hashCode();
        }
        if (getCustom6() != null) {
            _hashCode += getCustom6().hashCode();
        }
        if (getEmailAddress() != null) {
            _hashCode += getEmailAddress().hashCode();
        }
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getCurrencyCode() != null) {
            _hashCode += getCurrencyCode().hashCode();
        }
        if (getPurchaseCardCustomerID() != null) {
            _hashCode += getPurchaseCardCustomerID().hashCode();
        }
        if (getPurchaseCardTaxAmount() != null) {
            _hashCode += getPurchaseCardTaxAmount().hashCode();
        }
        if (getPurchaseCardTaxExempt() != null) {
            _hashCode += getPurchaseCardTaxExempt().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TransactionData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ReferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ticketNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TicketNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MCSTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "MCSTransactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gatewayID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "GatewayID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thirdPartyMerchID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ThirdPartyMerchID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thirdPartySubID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ThirdPartySubID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("third3RdPartyPin");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Third3rdPartyPin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CardHolderName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom1");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom2");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom3");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom4");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom5");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom5"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custom6");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Custom6"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "EmailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CurrencyCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseCardCustomerID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "PurchaseCardCustomerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseCardTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "PurchaseCardTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseCardTaxExempt");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "PurchaseCardTaxExempt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

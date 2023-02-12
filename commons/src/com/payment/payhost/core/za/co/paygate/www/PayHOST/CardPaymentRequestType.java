/**
 * CardPaymentRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class CardPaymentRequestType  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PersonType customer;

    private org.apache.axis.types.Token cardNumber;

    private org.apache.axis.types.Token cardExpiryDate;

    //private org.apache.axis.types.Token vaultId;

    private org.apache.axis.types.Token cardIssueDate;

    private org.apache.axis.types.Token cardIssueNumber;

    private org.apache.axis.types.Token CVV;

    private java.lang.Boolean vault;

    private org.apache.axis.types.Token budgetPeriod;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectRequestType redirect;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderType order;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.ThreeDSecureType threeDSecure;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.RiskType risk;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields;

    private org.apache.axis.types.Token billingDescriptor;

    public CardPaymentRequestType() {
    }

    public CardPaymentRequestType(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PersonType customer,
           org.apache.axis.types.Token cardNumber,
           org.apache.axis.types.Token cardExpiryDate,
           //org.apache.axis.types.Token vaultId,
           org.apache.axis.types.Token cardIssueDate,
           org.apache.axis.types.Token cardIssueNumber,
           org.apache.axis.types.Token CVV,
           java.lang.Boolean vault,
           org.apache.axis.types.Token budgetPeriod,
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectRequestType redirect,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderType order,
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.ThreeDSecureType threeDSecure,
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.RiskType risk,
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields,
           org.apache.axis.types.Token billingDescriptor) {
           this.account = account;
           this.customer = customer;
           this.cardNumber = cardNumber;
           this.cardExpiryDate = cardExpiryDate;
           //this.vaultId = vaultId;
           this.cardIssueDate = cardIssueDate;
           this.cardIssueNumber = cardIssueNumber;
           this.CVV = CVV;
           this.vault = vault;
           this.budgetPeriod = budgetPeriod;
           //this.redirect = redirect;
           this.order = order;
           //this.threeDSecure = threeDSecure;
           //this.risk = risk;
           //this.userDefinedFields = userDefinedFields;
           this.billingDescriptor = billingDescriptor;
    }


    /**
     * Gets the account value for this CardPaymentRequestType.
     * 
     * @return account
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType getAccount() {
        return account;
    }


    /**
     * Sets the account value for this CardPaymentRequestType.
     * 
     * @param account
     */
    public void setAccount(com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account) {
        this.account = account;
    }


    /**
     * Gets the customer value for this CardPaymentRequestType.
     * 
     * @return customer
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PersonType getCustomer() {
        return customer;
    }


    /**
     * Sets the customer value for this CardPaymentRequestType.
     * 
     * @param customer
     */
    public void setCustomer(com.payment.payhost.core.za.co.paygate.www.PayHOST.PersonType customer) {
        this.customer = customer;
    }


    /**
     * Gets the cardNumber value for this CardPaymentRequestType.
     * 
     * @return cardNumber
     */
    public org.apache.axis.types.Token getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this CardPaymentRequestType.
     * 
     * @param cardNumber
     */
    public void setCardNumber(org.apache.axis.types.Token cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the cardExpiryDate value for this CardPaymentRequestType.
     * 
     * @return cardExpiryDate
     */
    public org.apache.axis.types.Token getCardExpiryDate() {
        return cardExpiryDate;
    }


    /**
     * Sets the cardExpiryDate value for this CardPaymentRequestType.
     * 
     * @param cardExpiryDate
     */
    public void setCardExpiryDate(org.apache.axis.types.Token cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }


    /**
     * Gets the vaultId value for this CardPaymentRequestType.
     * 
     * @return vaultId
     */
    /*public org.apache.axis.types.Token getVaultId() {
        return vaultId;
    }*/


    /**
     * Sets the vaultId value for this CardPaymentRequestType.
     * 
     * @param vaultId
     */
    /*public void setVaultId(org.apache.axis.types.Token vaultId) {
        this.vaultId = vaultId;
    }*/


    /**
     * Gets the cardIssueDate value for this CardPaymentRequestType.
     * 
     * @return cardIssueDate
     */
    public org.apache.axis.types.Token getCardIssueDate() {
        return cardIssueDate;
    }


    /**
     * Sets the cardIssueDate value for this CardPaymentRequestType.
     * 
     * @param cardIssueDate
     */
    public void setCardIssueDate(org.apache.axis.types.Token cardIssueDate) {
        this.cardIssueDate = cardIssueDate;
    }


    /**
     * Gets the cardIssueNumber value for this CardPaymentRequestType.
     * 
     * @return cardIssueNumber
     */
    public org.apache.axis.types.Token getCardIssueNumber() {
        return cardIssueNumber;
    }


    /**
     * Sets the cardIssueNumber value for this CardPaymentRequestType.
     * 
     * @param cardIssueNumber
     */
    public void setCardIssueNumber(org.apache.axis.types.Token cardIssueNumber) {
        this.cardIssueNumber = cardIssueNumber;
    }


    /**
     * Gets the CVV value for this CardPaymentRequestType.
     * 
     * @return CVV
     */
    public org.apache.axis.types.Token getCVV() {
        return CVV;
    }


    /**
     * Sets the CVV value for this CardPaymentRequestType.
     * 
     * @param CVV
     */
    public void setCVV(org.apache.axis.types.Token CVV) {
        this.CVV = CVV;
    }


    /**
     * Gets the vault value for this CardPaymentRequestType.
     * 
     * @return vault
     */
    public java.lang.Boolean getVault() {
        return vault;
    }


    /**
     * Sets the vault value for this CardPaymentRequestType.
     * 
     * @param vault
     */
    public void setVault(java.lang.Boolean vault) {
        this.vault = vault;
    }


    /**
     * Gets the budgetPeriod value for this CardPaymentRequestType.
     * 
     * @return budgetPeriod
     */
    public org.apache.axis.types.Token getBudgetPeriod() {
        return budgetPeriod;
    }


    /**
     * Sets the budgetPeriod value for this CardPaymentRequestType.
     * 
     * @param budgetPeriod
     */
    public void setBudgetPeriod(org.apache.axis.types.Token budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }


    /**
     * Gets the redirect value for this CardPaymentRequestType.
     * 
     * @return redirect
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectRequestType getRedirect() {
        return redirect;
    }*/


    /**
     * Sets the redirect value for this CardPaymentRequestType.
     * 
     * @param redirect
     */
    /*public void setRedirect(com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectRequestType redirect) {
        this.redirect = redirect;
    }*/


    /**
     * Gets the order value for this CardPaymentRequestType.
     * 
     * @return order
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderType getOrder() {
        return order;
    }


    /**
     * Sets the order value for this CardPaymentRequestType.
     * 
     * @param order
     */
    public void setOrder(com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderType order) {
        this.order = order;
    }


    /**
     * Gets the threeDSecure value for this CardPaymentRequestType.
     * 
     * @return threeDSecure
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.ThreeDSecureType getThreeDSecure() {
        return threeDSecure;
    }*/


    /**
     * Sets the threeDSecure value for this CardPaymentRequestType.
     * 
     * @param threeDSecure
     */
    /*public void setThreeDSecure(com.payment.payhost.core.za.co.paygate.www.PayHOST.ThreeDSecureType threeDSecure) {
        this.threeDSecure = threeDSecure;
    }*/


    /**
     * Gets the risk value for this CardPaymentRequestType.
     * 
     * @return risk
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.RiskType getRisk() {
        return risk;
    }*/


    /**
     * Sets the risk value for this CardPaymentRequestType.
     * 
     * @param risk
     */
    /*public void setRisk(com.payment.payhost.core.za.co.paygate.www.PayHOST.RiskType risk) {

        this.risk = risk;
    }*/


    /**
     * Gets the userDefinedFields value for this CardPaymentRequestType.
     * 
     * @return userDefinedFields
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] getUserDefinedFields() {
        return userDefinedFields;
    }*/


    /**
     * Sets the userDefinedFields value for this CardPaymentRequestType.
     * 
     * @param userDefinedFields
     */
    /*public void setUserDefinedFields(com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields) {
        this.userDefinedFields = userDefinedFields;
    }*/

    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType getUserDefinedFields(int i) {
        return this.userDefinedFields[i];
    }*/

    /*public void setUserDefinedFields(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType _value) {
        this.userDefinedFields[i] = _value;
    }*/


    /**
     * Gets the billingDescriptor value for this CardPaymentRequestType.
     * 
     * @return billingDescriptor
     */
    public org.apache.axis.types.Token getBillingDescriptor() {
        return billingDescriptor;
    }


    /**
     * Sets the billingDescriptor value for this CardPaymentRequestType.
     * 
     * @param billingDescriptor
     */
    public void setBillingDescriptor(org.apache.axis.types.Token billingDescriptor) {
        this.billingDescriptor = billingDescriptor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CardPaymentRequestType)) return true;
        CardPaymentRequestType other = (CardPaymentRequestType) obj;
        if (obj == null) return true;
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
            ((this.customer==null && other.getCustomer()==null) || 
             (this.customer!=null &&
              this.customer.equals(other.getCustomer()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.cardExpiryDate==null && other.getCardExpiryDate()==null) || 
             (this.cardExpiryDate!=null &&
              this.cardExpiryDate.equals(other.getCardExpiryDate()))) /*&&
            ((this.vaultId==null && other.getVaultId()==null) || 
             (this.vaultId!=null &&
              this.vaultId.equals(other.getVaultId())))*/ &&
            ((this.cardIssueDate==null && other.getCardIssueDate()==null) || 
             (this.cardIssueDate!=null &&
              this.cardIssueDate.equals(other.getCardIssueDate()))) &&
            ((this.cardIssueNumber==null && other.getCardIssueNumber()==null) || 
             (this.cardIssueNumber!=null &&
              this.cardIssueNumber.equals(other.getCardIssueNumber()))) &&
            ((this.CVV==null && other.getCVV()==null) || 
             (this.CVV!=null &&
              this.CVV.equals(other.getCVV()))) &&
            ((this.vault==null && other.getVault()==null) || 
             (this.vault!=null &&
              this.vault.equals(other.getVault()))) &&
            ((this.budgetPeriod==null && other.getBudgetPeriod()==null) || 
             (this.budgetPeriod!=null &&
              this.budgetPeriod.equals(other.getBudgetPeriod()))) &&
            /*((this.redirect==null && other.getRedirect()==null) ||
             (this.redirect!=null &&
              this.redirect.equals(other.getRedirect()))) &&*/
            ((this.order==null && other.getOrder()==null) || 
             (this.order!=null &&
              this.order.equals(other.getOrder()))) &&
            /*((this.threeDSecure==null && other.getThreeDSecure()==null) ||
             (this.threeDSecure!=null &&
              this.threeDSecure.equals(other.getThreeDSecure()))) &&
            ((this.risk==null && other.getRisk()==null) || 
             (this.risk!=null &&
              this.risk.equals(other.getRisk()))) &&
            ((this.userDefinedFields==null && other.getUserDefinedFields()==null) || 
             (this.userDefinedFields!=null &&
              java.util.Arrays.equals(this.userDefinedFields, other.getUserDefinedFields()))) &&*/
            ((this.billingDescriptor==null && other.getBillingDescriptor()==null) || 
             (this.billingDescriptor!=null &&
              this.billingDescriptor.equals(other.getBillingDescriptor())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = true;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getCustomer() != null) {
            _hashCode += getCustomer().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getCardExpiryDate() != null) {
            _hashCode += getCardExpiryDate().hashCode();
        }
        /*if (getVaultId() != null) {
            _hashCode += getVaultId().hashCode();
        }*/
        if (getCardIssueDate() != null) {
            _hashCode += getCardIssueDate().hashCode();
        }
        if (getCardIssueNumber() != null) {
            _hashCode += getCardIssueNumber().hashCode();
        }
        if (getCVV() != null) {
            _hashCode += getCVV().hashCode();
        }
        if (getVault() != null) {
            _hashCode += getVault().hashCode();
        }
        if (getBudgetPeriod() != null) {
            _hashCode += getBudgetPeriod().hashCode();
        }
        /*if (getRedirect() != null) {
            _hashCode += getRedirect().hashCode();
        }*/
        if (getOrder() != null) {
            _hashCode += getOrder().hashCode();
        }
        /*if (getThreeDSecure() != null) {
            _hashCode += getThreeDSecure().hashCode();
        }
        if (getRisk() != null) {
            _hashCode += getRisk().hashCode();
        }
        if (getUserDefinedFields() != null) {
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
        if (getBillingDescriptor() != null) {
            _hashCode += getBillingDescriptor().hashCode();
        }
        __hashCodeCalc = true;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CardPaymentRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayGateAccountType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Customer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PersonType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardExpiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        /*elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vaultId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VaultId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);*/
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardIssueDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardIssueDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardIssueNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardIssueNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CVV");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CVV"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vault");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Vault"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budgetPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BudgetPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("redirect");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Redirect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RedirectRequestType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("threeDSecure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ThreeDSecure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ThreeDSecureType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("risk");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Risk"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RiskType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userDefinedFields");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "UserDefinedFields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "KeyValueType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BillingDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
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

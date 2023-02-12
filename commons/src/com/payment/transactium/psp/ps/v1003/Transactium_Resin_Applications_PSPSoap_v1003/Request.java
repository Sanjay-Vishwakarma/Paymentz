/**
 * Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class Request  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Request.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authentication");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Authentication"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.AuthenticationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Billing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.BillingType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("card");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Card"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.CardType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Customer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.CustomerType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fraudScreening");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "FraudScreening"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.FraudScreeningType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linked");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Linked"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.LinkedType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.OptionsType"));
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
        elemField.setFieldName("shipping");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Shipping"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.ShippingType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("threeDS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ThreeDS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.ThreeDSType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Transaction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.TransactionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestBillingType billing;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCardType card;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCustomerType customer;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestFraudScreeningType fraudScreening;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestLinkedType linked;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestOptionsType options;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestShippingType shipping;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestThreeDSType threeDS;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestTransactionType transaction;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public Request() {
    }


    public Request(
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestBillingType billing,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCardType card,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCustomerType customer,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestFraudScreeningType fraudScreening,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestLinkedType linked,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestOptionsType options,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestShippingType shipping,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestThreeDSType threeDS,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestTransactionType transaction) {
           this.authentication = authentication;
           this.billing = billing;
           this.card = card;
           this.customer = customer;
           this.fraudScreening = fraudScreening;
           this.linked = linked;
           this.options = options;
           this.references = references;
           this.shipping = shipping;
           this.threeDS = threeDS;
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
     * Gets the authentication value for this Request.
     *
     * @return authentication
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType getAuthentication() {
        return authentication;
    }

    /**
     * Sets the authentication value for this Request.
     *
     * @param authentication
     */
    public void setAuthentication(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestAuthenticationType authentication) {
        this.authentication = authentication;
    }

    /**
     * Gets the billing value for this Request.
     *
     * @return billing
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestBillingType getBilling() {
        return billing;
    }

    /**
     * Sets the billing value for this Request.
     *
     * @param billing
     */
    public void setBilling(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestBillingType billing) {
        this.billing = billing;
    }

    /**
     * Gets the card value for this Request.
     *
     * @return card
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCardType getCard() {
        return card;
    }

    /**
     * Sets the card value for this Request.
     *
     * @param card
     */
    public void setCard(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCardType card) {
        this.card = card;
    }

    /**
     * Gets the customer value for this Request.
     *
     * @return customer
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCustomerType getCustomer() {
        return customer;
    }

    /**
     * Sets the customer value for this Request.
     *
     * @param customer
     */
    public void setCustomer(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestCustomerType customer) {
        this.customer = customer;
    }

    /**
     * Gets the fraudScreening value for this Request.
     *
     * @return fraudScreening
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestFraudScreeningType getFraudScreening() {
        return fraudScreening;
    }

    /**
     * Sets the fraudScreening value for this Request.
     *
     * @param fraudScreening
     */
    public void setFraudScreening(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestFraudScreeningType fraudScreening) {
        this.fraudScreening = fraudScreening;
    }

    /**
     * Gets the linked value for this Request.
     *
     * @return linked
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestLinkedType getLinked() {
        return linked;
    }

    /**
     * Sets the linked value for this Request.
     *
     * @param linked
     */
    public void setLinked(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestLinkedType linked) {
        this.linked = linked;
    }

    /**
     * Gets the options value for this Request.
     *
     * @return options
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestOptionsType getOptions() {
        return options;
    }

    /**
     * Sets the options value for this Request.
     *
     * @param options
     */
    public void setOptions(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestOptionsType options) {
        this.options = options;
    }

    /**
     * Gets the references value for this Request.
     *
     * @return references
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType getReferences() {
        return references;
    }

    /**
     * Sets the references value for this Request.
     *
     * @param references
     */
    public void setReferences(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestReferencesType references) {
        this.references = references;
    }

    /**
     * Gets the shipping value for this Request.
     *
     * @return shipping
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestShippingType getShipping() {
        return shipping;
    }

    /**
     * Sets the shipping value for this Request.
     *
     * @param shipping
     */
    public void setShipping(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestShippingType shipping) {
        this.shipping = shipping;
    }

    /**
     * Gets the threeDS value for this Request.
     *
     * @return threeDS
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestThreeDSType getThreeDS() {
        return threeDS;
    }

    /**
     * Sets the threeDS value for this Request.
     *
     * @param threeDS
     */
    public void setThreeDS(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestThreeDSType threeDS) {
        this.threeDS = threeDS;
    }

    /**
     * Gets the transaction value for this Request.
     *
     * @return transaction
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestTransactionType getTransaction() {
        return transaction;
    }

    /**
     * Sets the transaction value for this Request.
     *
     * @param transaction
     */
    public void setTransaction(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.RequestTransactionType transaction) {
        this.transaction = transaction;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Request)) return false;
        Request other = (Request) obj;
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
            ((this.billing==null && other.getBilling()==null) ||
             (this.billing!=null &&
              this.billing.equals(other.getBilling()))) &&
            ((this.card==null && other.getCard()==null) ||
             (this.card!=null &&
              this.card.equals(other.getCard()))) &&
            ((this.customer==null && other.getCustomer()==null) ||
             (this.customer!=null &&
              this.customer.equals(other.getCustomer()))) &&
            ((this.fraudScreening==null && other.getFraudScreening()==null) ||
             (this.fraudScreening!=null &&
              this.fraudScreening.equals(other.getFraudScreening()))) &&
            ((this.linked==null && other.getLinked()==null) ||
             (this.linked!=null &&
              this.linked.equals(other.getLinked()))) &&
            ((this.options==null && other.getOptions()==null) ||
             (this.options!=null &&
              this.options.equals(other.getOptions()))) &&
            ((this.references==null && other.getReferences()==null) ||
             (this.references!=null &&
              this.references.equals(other.getReferences()))) &&
            ((this.shipping==null && other.getShipping()==null) ||
             (this.shipping!=null &&
              this.shipping.equals(other.getShipping()))) &&
            ((this.threeDS==null && other.getThreeDS()==null) ||
             (this.threeDS!=null &&
              this.threeDS.equals(other.getThreeDS()))) &&
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
        if (getBilling() != null) {
            _hashCode += getBilling().hashCode();
        }
        if (getCard() != null) {
            _hashCode += getCard().hashCode();
        }
        if (getCustomer() != null) {
            _hashCode += getCustomer().hashCode();
        }
        if (getFraudScreening() != null) {
            _hashCode += getFraudScreening().hashCode();
        }
        if (getLinked() != null) {
            _hashCode += getLinked().hashCode();
        }
        if (getOptions() != null) {
            _hashCode += getOptions().hashCode();
        }
        if (getReferences() != null) {
            _hashCode += getReferences().hashCode();
        }
        if (getShipping() != null) {
            _hashCode += getShipping().hashCode();
        }
        if (getThreeDS() != null) {
            _hashCode += getThreeDS().hashCode();
        }
        if (getTransaction() != null) {
            _hashCode += getTransaction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

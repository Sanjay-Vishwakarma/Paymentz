/**
 * RequestReferencesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestReferencesType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestReferencesType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.ReferencesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("client");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Client"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostedPaymentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "HostedPaymentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Merchant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recurringPaymentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "RecurringPaymentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shoppingCartId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ShoppingCartId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String client;
    private org.apache.axis.types.UnsignedInt hostedPaymentId;
    private String merchant;
    private String order;
    private org.apache.axis.types.UnsignedInt recurringPaymentId;
    private org.apache.axis.types.UnsignedInt shoppingCartId;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestReferencesType() {
    }


    public RequestReferencesType(
           String client,
           org.apache.axis.types.UnsignedInt hostedPaymentId,
           String merchant,
           String order,
           org.apache.axis.types.UnsignedInt recurringPaymentId,
           org.apache.axis.types.UnsignedInt shoppingCartId) {
           this.client = client;
           this.hostedPaymentId = hostedPaymentId;
           this.merchant = merchant;
           this.order = order;
           this.recurringPaymentId = recurringPaymentId;
           this.shoppingCartId = shoppingCartId;
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
     * Gets the client value for this RequestReferencesType.
     *
     * @return client
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets the client value for this RequestReferencesType.
     *
     * @param client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Gets the hostedPaymentId value for this RequestReferencesType.
     *
     * @return hostedPaymentId
     */
    public org.apache.axis.types.UnsignedInt getHostedPaymentId() {
        return hostedPaymentId;
    }

    /**
     * Sets the hostedPaymentId value for this RequestReferencesType.
     *
     * @param hostedPaymentId
     */
    public void setHostedPaymentId(org.apache.axis.types.UnsignedInt hostedPaymentId) {
        this.hostedPaymentId = hostedPaymentId;
    }

    /**
     * Gets the merchant value for this RequestReferencesType.
     *
     * @return merchant
     */
    public String getMerchant() {
        return merchant;
    }

    /**
     * Sets the merchant value for this RequestReferencesType.
     *
     * @param merchant
     */
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    /**
     * Gets the order value for this RequestReferencesType.
     *
     * @return order
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the order value for this RequestReferencesType.
     *
     * @param order
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Gets the recurringPaymentId value for this RequestReferencesType.
     *
     * @return recurringPaymentId
     */
    public org.apache.axis.types.UnsignedInt getRecurringPaymentId() {
        return recurringPaymentId;
    }

    /**
     * Sets the recurringPaymentId value for this RequestReferencesType.
     *
     * @param recurringPaymentId
     */
    public void setRecurringPaymentId(org.apache.axis.types.UnsignedInt recurringPaymentId) {
        this.recurringPaymentId = recurringPaymentId;
    }

    /**
     * Gets the shoppingCartId value for this RequestReferencesType.
     *
     * @return shoppingCartId
     */
    public org.apache.axis.types.UnsignedInt getShoppingCartId() {
        return shoppingCartId;
    }

    /**
     * Sets the shoppingCartId value for this RequestReferencesType.
     *
     * @param shoppingCartId
     */
    public void setShoppingCartId(org.apache.axis.types.UnsignedInt shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestReferencesType)) return false;
        RequestReferencesType other = (RequestReferencesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.client==null && other.getClient()==null) ||
             (this.client!=null &&
              this.client.equals(other.getClient()))) &&
            ((this.hostedPaymentId==null && other.getHostedPaymentId()==null) ||
             (this.hostedPaymentId!=null &&
              this.hostedPaymentId.equals(other.getHostedPaymentId()))) &&
            ((this.merchant==null && other.getMerchant()==null) ||
             (this.merchant!=null &&
              this.merchant.equals(other.getMerchant()))) &&
            ((this.order==null && other.getOrder()==null) ||
             (this.order!=null &&
              this.order.equals(other.getOrder()))) &&
            ((this.recurringPaymentId==null && other.getRecurringPaymentId()==null) ||
             (this.recurringPaymentId!=null &&
              this.recurringPaymentId.equals(other.getRecurringPaymentId()))) &&
            ((this.shoppingCartId==null && other.getShoppingCartId()==null) ||
             (this.shoppingCartId!=null &&
              this.shoppingCartId.equals(other.getShoppingCartId())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getClient() != null) {
            _hashCode += getClient().hashCode();
        }
        if (getHostedPaymentId() != null) {
            _hashCode += getHostedPaymentId().hashCode();
        }
        if (getMerchant() != null) {
            _hashCode += getMerchant().hashCode();
        }
        if (getOrder() != null) {
            _hashCode += getOrder().hashCode();
        }
        if (getRecurringPaymentId() != null) {
            _hashCode += getRecurringPaymentId().hashCode();
        }
        if (getShoppingCartId() != null) {
            _hashCode += getShoppingCartId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}

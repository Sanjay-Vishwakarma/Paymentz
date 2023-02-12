/**
 * OrderType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class OrderType  implements java.io.Serializable {
    private org.apache.axis.types.Token merchantOrderId;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency;

    private int amount;

    private java.lang.Integer discount;

    private java.util.Calendar transactionDate;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.BillingDetailsType billingDetails;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.ShippingDetailsType shippingDetails;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.AirlineBookingDetailsType airlineBookingDetails;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType[] orderItems;

    private org.apache.axis.types.Token locale;

    public OrderType() {
    }

    public OrderType(
           org.apache.axis.types.Token merchantOrderId,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency,
           int amount,
           java.lang.Integer discount,
           java.util.Calendar transactionDate,
           //com.payment.payhost.core.za.co.paygate.www.PayHOST.BillingDetailsType billingDetails,
           //com.payment.payhost.core.za.co.paygate.www.PayHOST.ShippingDetailsType shippingDetails,
           //com.payment.payhost.core.za.co.paygate.www.PayHOST.AirlineBookingDetailsType airlineBookingDetails,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType[] orderItems,
           org.apache.axis.types.Token locale) {
           this.merchantOrderId = merchantOrderId;
           this.currency = currency;
           this.amount = amount;
           this.discount = discount;
           this.transactionDate = transactionDate;
           //this.billingDetails = billingDetails;
           //this.shippingDetails = shippingDetails;
           //this.airlineBookingDetails = airlineBookingDetails;
           this.orderItems = orderItems;
           this.locale = locale;
    }


    /**
     * Gets the merchantOrderId value for this OrderType.
     * 
     * @return merchantOrderId
     */
    public org.apache.axis.types.Token getMerchantOrderId() {
        return merchantOrderId;
    }


    /**
     * Sets the merchantOrderId value for this OrderType.
     * 
     * @param merchantOrderId
     */
    public void setMerchantOrderId(org.apache.axis.types.Token merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }


    /**
     * Gets the currency value for this OrderType.
     * 
     * @return currency
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this OrderType.
     * 
     * @param currency
     */
    public void setCurrency(com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType currency) {
        this.currency = currency;
    }


    /**
     * Gets the amount value for this OrderType.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this OrderType.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


    /**
     * Gets the discount value for this OrderType.
     * 
     * @return discount
     */
    public java.lang.Integer getDiscount() {
        return discount;
    }


    /**
     * Sets the discount value for this OrderType.
     * 
     * @param discount
     */
    public void setDiscount(java.lang.Integer discount) {
        this.discount = discount;
    }


    /**
     * Gets the transactionDate value for this OrderType.
     * 
     * @return transactionDate
     */
    public java.util.Calendar getTransactionDate() {
        return transactionDate;
    }


    /**
     * Sets the transactionDate value for this OrderType.
     * 
     * @param transactionDate
     */
    public void setTransactionDate(java.util.Calendar transactionDate) {
        this.transactionDate = transactionDate;
    }


    /**
     * Sets the billingDetails value for this OrderType.
     *
     * @param billingDetails
     */
    /*public void setBillingDetails(com.payment.payhost.core.za.co.paygate.www.PayHOST.BillingDetailsType billingDetails) {
        this.billingDetails = billingDetails;
    }
*/

    /**
     * Gets the billingDetails value for this OrderType.
     *
     * @return billingDetails
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.BillingDetailsType getBillingDetails() {
        return billingDetails;
    }
*/

    /**
     * Gets the shippingDetails value for this OrderType.
     * 
     * @return shippingDetails
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.ShippingDetailsType getShippingDetails() {
        return shippingDetails;
    }
*/

    /**
     * Sets the shippingDetails value for this OrderType.
     * 
     * @param shippingDetails
     */
    /*public void setShippingDetails(com.payment.payhost.core.za.co.paygate.www.PayHOST.ShippingDetailsType shippingDetails) {
        this.shippingDetails = shippingDetails;
    }
*/

    /**
     * Gets the airlineBookingDetails value for this OrderType.
     * 
     * @return airlineBookingDetails
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.AirlineBookingDetailsType getAirlineBookingDetails() {
        return airlineBookingDetails;
    }
*/

    /**
     * Sets the airlineBookingDetails value for this OrderType.
     * 
     * @param airlineBookingDetails
     */
    /*public void setAirlineBookingDetails(com.payment.payhost.core.za.co.paygate.www.PayHOST.AirlineBookingDetailsType airlineBookingDetails) {
        this.airlineBookingDetails = airlineBookingDetails;
    }
*/

    /**
     * Gets the orderItems value for this OrderType.
     * 
     * @return orderItems
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType[] getOrderItems() {
        return orderItems;
    }


    /**
     * Sets the orderItems value for this OrderType.
     * 
     * @param orderItems
     */
    public void setOrderItems(com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType[] orderItems) {
        this.orderItems = orderItems;
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType getOrderItems(int i) {
        return this.orderItems[i];
    }

    public void setOrderItems(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType _value) {
        this.orderItems[i] = _value;
    }


    /**
     * Gets the locale value for this OrderType.
     * 
     * @return locale
     */
    public org.apache.axis.types.Token getLocale() {
        return locale;
    }


    /**
     * Sets the locale value for this OrderType.
     * 
     * @param locale
     */
    public void setLocale(org.apache.axis.types.Token locale) {
        this.locale = locale;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OrderType)) return false;
        OrderType other = (OrderType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchantOrderId==null && other.getMerchantOrderId()==null) || 
             (this.merchantOrderId!=null &&
              this.merchantOrderId.equals(other.getMerchantOrderId()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            this.amount == other.getAmount() &&
            ((this.discount==null && other.getDiscount()==null) || 
             (this.discount!=null &&
              this.discount.equals(other.getDiscount()))) &&
            ((this.transactionDate==null && other.getTransactionDate()==null) || 
             (this.transactionDate!=null &&
              this.transactionDate.equals(other.getTransactionDate()))) /*&&
            ((this.billingDetails==null && other.getBillingDetails()==null) || 
             (this.billingDetails!=null &&
              this.billingDetails.equals(other.getBillingDetails()))) &&
            ((this.shippingDetails==null && other.getShippingDetails()==null) || 
             (this.shippingDetails!=null &&
              this.shippingDetails.equals(other.getShippingDetails()))) &&
            ((this.airlineBookingDetails==null && other.getAirlineBookingDetails()==null) || 
             (this.airlineBookingDetails!=null &&
              this.airlineBookingDetails.equals(other.getAirlineBookingDetails())))*/ &&
            ((this.orderItems==null && other.getOrderItems()==null) || 
             (this.orderItems!=null &&
              java.util.Arrays.equals(this.orderItems, other.getOrderItems()))) &&
            ((this.locale==null && other.getLocale()==null) || 
             (this.locale!=null &&
              this.locale.equals(other.getLocale())));
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
        if (getMerchantOrderId() != null) {
            _hashCode += getMerchantOrderId().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        _hashCode += getAmount();
        if (getDiscount() != null) {
            _hashCode += getDiscount().hashCode();
        }
        if (getTransactionDate() != null) {
            _hashCode += getTransactionDate().hashCode();
        }
        /*if (getBillingDetails() != null) {
            _hashCode += getBillingDetails().hashCode();
        }
        if (getShippingDetails() != null) {
            _hashCode += getShippingDetails().hashCode();
        }
        if (getAirlineBookingDetails() != null) {
            _hashCode += getAirlineBookingDetails().hashCode();
        }*/
        if (getOrderItems() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOrderItems());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOrderItems(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLocale() != null) {
            _hashCode += getLocale().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OrderType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantOrderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "MerchantOrderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CurrencyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("discount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Discount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BillingDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BillingDetailsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shippingDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ShippingDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ShippingDetailsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("airlineBookingDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AirlineBookingDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AirlineBookingDetailsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderItemType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locale");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Locale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
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

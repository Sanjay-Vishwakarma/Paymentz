/**
 * OrderItemType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class OrderItemType  implements java.io.Serializable {
    private org.apache.axis.types.Token productCode;

    private org.apache.axis.types.Token productDescription;

    private org.apache.axis.types.Token productCategory;

    private org.apache.axis.types.Token productRisk;

    private org.apache.axis.types.UnsignedInt orderQuantity;

    private java.math.BigDecimal unitPrice;

    private org.apache.axis.types.Token currency;

    public OrderItemType() {
    }

    public OrderItemType(
           org.apache.axis.types.Token productCode,
           org.apache.axis.types.Token productDescription,
           org.apache.axis.types.Token productCategory,
           org.apache.axis.types.Token productRisk,
           org.apache.axis.types.UnsignedInt orderQuantity,
           java.math.BigDecimal unitPrice,
           org.apache.axis.types.Token currency) {
           this.productCode = productCode;
           this.productDescription = productDescription;
           this.productCategory = productCategory;
           this.productRisk = productRisk;
           this.orderQuantity = orderQuantity;
           this.unitPrice = unitPrice;
           this.currency = currency;
    }


    /**
     * Gets the productCode value for this OrderItemType.
     * 
     * @return productCode
     */
    public org.apache.axis.types.Token getProductCode() {
        return productCode;
    }


    /**
     * Sets the productCode value for this OrderItemType.
     * 
     * @param productCode
     */
    public void setProductCode(org.apache.axis.types.Token productCode) {
        this.productCode = productCode;
    }


    /**
     * Gets the productDescription value for this OrderItemType.
     * 
     * @return productDescription
     */
    public org.apache.axis.types.Token getProductDescription() {
        return productDescription;
    }


    /**
     * Sets the productDescription value for this OrderItemType.
     * 
     * @param productDescription
     */
    public void setProductDescription(org.apache.axis.types.Token productDescription) {
        this.productDescription = productDescription;
    }


    /**
     * Gets the productCategory value for this OrderItemType.
     * 
     * @return productCategory
     */
    public org.apache.axis.types.Token getProductCategory() {
        return productCategory;
    }


    /**
     * Sets the productCategory value for this OrderItemType.
     * 
     * @param productCategory
     */
    public void setProductCategory(org.apache.axis.types.Token productCategory) {
        this.productCategory = productCategory;
    }


    /**
     * Gets the productRisk value for this OrderItemType.
     * 
     * @return productRisk
     */
    public org.apache.axis.types.Token getProductRisk() {
        return productRisk;
    }


    /**
     * Sets the productRisk value for this OrderItemType.
     * 
     * @param productRisk
     */
    public void setProductRisk(org.apache.axis.types.Token productRisk) {
        this.productRisk = productRisk;
    }


    /**
     * Gets the orderQuantity value for this OrderItemType.
     * 
     * @return orderQuantity
     */
    public org.apache.axis.types.UnsignedInt getOrderQuantity() {
        return orderQuantity;
    }


    /**
     * Sets the orderQuantity value for this OrderItemType.
     * 
     * @param orderQuantity
     */
    public void setOrderQuantity(org.apache.axis.types.UnsignedInt orderQuantity) {
        this.orderQuantity = orderQuantity;
    }


    /**
     * Gets the unitPrice value for this OrderItemType.
     * 
     * @return unitPrice
     */
    public java.math.BigDecimal getUnitPrice() {
        return unitPrice;
    }


    /**
     * Sets the unitPrice value for this OrderItemType.
     * 
     * @param unitPrice
     */
    public void setUnitPrice(java.math.BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }


    /**
     * Gets the currency value for this OrderItemType.
     * 
     * @return currency
     */
    public org.apache.axis.types.Token getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this OrderItemType.
     * 
     * @param currency
     */
    public void setCurrency(org.apache.axis.types.Token currency) {
        this.currency = currency;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OrderItemType)) return false;
        OrderItemType other = (OrderItemType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.productCode==null && other.getProductCode()==null) || 
             (this.productCode!=null &&
              this.productCode.equals(other.getProductCode()))) &&
            ((this.productDescription==null && other.getProductDescription()==null) || 
             (this.productDescription!=null &&
              this.productDescription.equals(other.getProductDescription()))) &&
            ((this.productCategory==null && other.getProductCategory()==null) || 
             (this.productCategory!=null &&
              this.productCategory.equals(other.getProductCategory()))) &&
            ((this.productRisk==null && other.getProductRisk()==null) || 
             (this.productRisk!=null &&
              this.productRisk.equals(other.getProductRisk()))) &&
            ((this.orderQuantity==null && other.getOrderQuantity()==null) || 
             (this.orderQuantity!=null &&
              this.orderQuantity.equals(other.getOrderQuantity()))) &&
            ((this.unitPrice==null && other.getUnitPrice()==null) || 
             (this.unitPrice!=null &&
              this.unitPrice.equals(other.getUnitPrice()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency())));
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
        if (getProductCode() != null) {
            _hashCode += getProductCode().hashCode();
        }
        if (getProductDescription() != null) {
            _hashCode += getProductDescription().hashCode();
        }
        if (getProductCategory() != null) {
            _hashCode += getProductCategory().hashCode();
        }
        if (getProductRisk() != null) {
            _hashCode += getProductRisk().hashCode();
        }
        if (getOrderQuantity() != null) {
            _hashCode += getOrderQuantity().hashCode();
        }
        if (getUnitPrice() != null) {
            _hashCode += getUnitPrice().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OrderItemType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderItemType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ProductCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ProductDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ProductCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productRisk");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ProductRisk"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderQuantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderQuantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitPrice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "UnitPrice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Currency"));
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

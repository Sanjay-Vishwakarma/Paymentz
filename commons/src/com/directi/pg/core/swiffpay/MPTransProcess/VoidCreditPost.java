/**
 * VoidCreditPost.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class VoidCreditPost  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private float amount;

    private java.lang.String orderid;

    private java.lang.String historyid;

    private java.lang.String ipaddress;

    private java.lang.String merchantpin;

    private java.lang.String merchantordernumber;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    private com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2;

    private com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant;

    public VoidCreditPost() {
    }

    public VoidCreditPost(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           float amount,
           java.lang.String orderid,
           java.lang.String historyid,
           java.lang.String ipaddress,
           java.lang.String merchantpin,
           java.lang.String merchantordernumber,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields,
           com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2,
           com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.amount = amount;
           this.orderid = orderid;
           this.historyid = historyid;
           this.ipaddress = ipaddress;
           this.merchantpin = merchantpin;
           this.merchantordernumber = merchantordernumber;
           this.customizedfields = customizedfields;
           this.purchasecardlevel2 = purchasecardlevel2;
           this.restaurant = restaurant;
    }


    /**
     * Gets the acctid value for this VoidCreditPost.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this VoidCreditPost.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this VoidCreditPost.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this VoidCreditPost.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this VoidCreditPost.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this VoidCreditPost.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the amount value for this VoidCreditPost.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this VoidCreditPost.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the orderid value for this VoidCreditPost.
     * 
     * @return orderid
     */
    public java.lang.String getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this VoidCreditPost.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.String orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the historyid value for this VoidCreditPost.
     * 
     * @return historyid
     */
    public java.lang.String getHistoryid() {
        return historyid;
    }


    /**
     * Sets the historyid value for this VoidCreditPost.
     * 
     * @param historyid
     */
    public void setHistoryid(java.lang.String historyid) {
        this.historyid = historyid;
    }


    /**
     * Gets the ipaddress value for this VoidCreditPost.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this VoidCreditPost.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the merchantpin value for this VoidCreditPost.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this VoidCreditPost.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the merchantordernumber value for this VoidCreditPost.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this VoidCreditPost.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the customizedfields value for this VoidCreditPost.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this VoidCreditPost.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }


    /**
     * Gets the purchasecardlevel2 value for this VoidCreditPost.
     * 
     * @return purchasecardlevel2
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 getPurchasecardlevel2() {
        return purchasecardlevel2;
    }


    /**
     * Sets the purchasecardlevel2 value for this VoidCreditPost.
     * 
     * @param purchasecardlevel2
     */
    public void setPurchasecardlevel2(com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2) {
        this.purchasecardlevel2 = purchasecardlevel2;
    }


    /**
     * Gets the restaurant value for this VoidCreditPost.
     * 
     * @return restaurant
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Restaurant getRestaurant() {
        return restaurant;
    }


    /**
     * Sets the restaurant value for this VoidCreditPost.
     * 
     * @param restaurant
     */
    public void setRestaurant(com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoidCreditPost)) return false;
        VoidCreditPost other = (VoidCreditPost) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.acctid==null && other.getAcctid()==null) || 
             (this.acctid!=null &&
              this.acctid.equals(other.getAcctid()))) &&
            ((this.accountkey==null && other.getAccountkey()==null) || 
             (this.accountkey!=null &&
              this.accountkey.equals(other.getAccountkey()))) &&
            ((this.subid==null && other.getSubid()==null) || 
             (this.subid!=null &&
              this.subid.equals(other.getSubid()))) &&
            this.amount == other.getAmount() &&
            ((this.orderid==null && other.getOrderid()==null) || 
             (this.orderid!=null &&
              this.orderid.equals(other.getOrderid()))) &&
            ((this.historyid==null && other.getHistoryid()==null) || 
             (this.historyid!=null &&
              this.historyid.equals(other.getHistoryid()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
            ((this.customizedfields==null && other.getCustomizedfields()==null) || 
             (this.customizedfields!=null &&
              this.customizedfields.equals(other.getCustomizedfields()))) &&
            ((this.purchasecardlevel2==null && other.getPurchasecardlevel2()==null) || 
             (this.purchasecardlevel2!=null &&
              this.purchasecardlevel2.equals(other.getPurchasecardlevel2()))) &&
            ((this.restaurant==null && other.getRestaurant()==null) || 
             (this.restaurant!=null &&
              this.restaurant.equals(other.getRestaurant())));
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
        if (getAcctid() != null) {
            _hashCode += getAcctid().hashCode();
        }
        if (getAccountkey() != null) {
            _hashCode += getAccountkey().hashCode();
        }
        if (getSubid() != null) {
            _hashCode += getSubid().hashCode();
        }
        _hashCode += new Float(getAmount()).hashCode();
        if (getOrderid() != null) {
            _hashCode += getOrderid().hashCode();
        }
        if (getHistoryid() != null) {
            _hashCode += getHistoryid().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getMerchantordernumber() != null) {
            _hashCode += getMerchantordernumber().hashCode();
        }
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        if (getPurchasecardlevel2() != null) {
            _hashCode += getPurchasecardlevel2().hashCode();
        }
        if (getRestaurant() != null) {
            _hashCode += getRestaurant().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoidCreditPost.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acctid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acctid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountkey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountkey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("historyid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "historyid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantpin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantpin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantordernumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantordernumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedfields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedfields"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchasecardlevel2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "purchasecardlevel2"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "PurchaseCardLevel2"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("restaurant");
        elemField.setXmlName(new javax.xml.namespace.QName("", "restaurant"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "Restaurant"));
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

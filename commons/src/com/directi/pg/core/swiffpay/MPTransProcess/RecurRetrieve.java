/**
 * RecurRetrieve.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class RecurRetrieve  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private java.lang.String orderid;

    private java.lang.String merchantpin;

    private java.lang.String ipaddress;

    public RecurRetrieve() {
    }

    public RecurRetrieve(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           java.lang.String orderid,
           java.lang.String merchantpin,
           java.lang.String ipaddress) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.orderid = orderid;
           this.merchantpin = merchantpin;
           this.ipaddress = ipaddress;
    }


    /**
     * Gets the acctid value for this RecurRetrieve.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this RecurRetrieve.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this RecurRetrieve.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this RecurRetrieve.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this RecurRetrieve.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this RecurRetrieve.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the orderid value for this RecurRetrieve.
     * 
     * @return orderid
     */
    public java.lang.String getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this RecurRetrieve.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.String orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the merchantpin value for this RecurRetrieve.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this RecurRetrieve.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the ipaddress value for this RecurRetrieve.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this RecurRetrieve.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecurRetrieve)) return false;
        RecurRetrieve other = (RecurRetrieve) obj;
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
            ((this.orderid==null && other.getOrderid()==null) || 
             (this.orderid!=null &&
              this.orderid.equals(other.getOrderid()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress())));
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
        if (getOrderid() != null) {
            _hashCode += getOrderid().hashCode();
        }
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecurRetrieve.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "RecurRetrieve"));
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
        elemField.setFieldName("orderid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderid"));
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
        elemField.setFieldName("ipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipaddress"));
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

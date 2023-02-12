/**
 * DebitReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class DebitReturn  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private float amount;

    private java.lang.String orderid;

    private java.lang.String historyid;

    private java.lang.String swipedata;

    private java.lang.String customerid;

    private java.lang.String ipaddress;

    private java.lang.String merchantpin;

    private java.lang.String merchantordernumber;

    private java.lang.String email;

    private java.lang.String currencycode;

    private java.lang.String memo;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    public DebitReturn() {
    }

    public DebitReturn(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           float amount,
           java.lang.String orderid,
           java.lang.String historyid,
           java.lang.String swipedata,
           java.lang.String customerid,
           java.lang.String ipaddress,
           java.lang.String merchantpin,
           java.lang.String merchantordernumber,
           java.lang.String email,
           java.lang.String currencycode,
           java.lang.String memo,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.amount = amount;
           this.orderid = orderid;
           this.historyid = historyid;
           this.swipedata = swipedata;
           this.customerid = customerid;
           this.ipaddress = ipaddress;
           this.merchantpin = merchantpin;
           this.merchantordernumber = merchantordernumber;
           this.email = email;
           this.currencycode = currencycode;
           this.memo = memo;
           this.customizedemail = customizedemail;
           this.customizedfields = customizedfields;
    }


    /**
     * Gets the acctid value for this DebitReturn.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this DebitReturn.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this DebitReturn.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this DebitReturn.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this DebitReturn.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this DebitReturn.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the amount value for this DebitReturn.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this DebitReturn.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the orderid value for this DebitReturn.
     * 
     * @return orderid
     */
    public java.lang.String getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this DebitReturn.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.String orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the historyid value for this DebitReturn.
     * 
     * @return historyid
     */
    public java.lang.String getHistoryid() {
        return historyid;
    }


    /**
     * Sets the historyid value for this DebitReturn.
     * 
     * @param historyid
     */
    public void setHistoryid(java.lang.String historyid) {
        this.historyid = historyid;
    }


    /**
     * Gets the swipedata value for this DebitReturn.
     * 
     * @return swipedata
     */
    public java.lang.String getSwipedata() {
        return swipedata;
    }


    /**
     * Sets the swipedata value for this DebitReturn.
     * 
     * @param swipedata
     */
    public void setSwipedata(java.lang.String swipedata) {
        this.swipedata = swipedata;
    }


    /**
     * Gets the customerid value for this DebitReturn.
     * 
     * @return customerid
     */
    public java.lang.String getCustomerid() {
        return customerid;
    }


    /**
     * Sets the customerid value for this DebitReturn.
     * 
     * @param customerid
     */
    public void setCustomerid(java.lang.String customerid) {
        this.customerid = customerid;
    }


    /**
     * Gets the ipaddress value for this DebitReturn.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this DebitReturn.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the merchantpin value for this DebitReturn.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this DebitReturn.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the merchantordernumber value for this DebitReturn.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this DebitReturn.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the email value for this DebitReturn.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this DebitReturn.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the currencycode value for this DebitReturn.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this DebitReturn.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the memo value for this DebitReturn.
     * 
     * @return memo
     */
    public java.lang.String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this DebitReturn.
     * 
     * @param memo
     */
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }


    /**
     * Gets the customizedemail value for this DebitReturn.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this DebitReturn.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the customizedfields value for this DebitReturn.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this DebitReturn.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DebitReturn)) return false;
        DebitReturn other = (DebitReturn) obj;
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
            ((this.swipedata==null && other.getSwipedata()==null) || 
             (this.swipedata!=null &&
              this.swipedata.equals(other.getSwipedata()))) &&
            ((this.customerid==null && other.getCustomerid()==null) || 
             (this.customerid!=null &&
              this.customerid.equals(other.getCustomerid()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.currencycode==null && other.getCurrencycode()==null) || 
             (this.currencycode!=null &&
              this.currencycode.equals(other.getCurrencycode()))) &&
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.customizedemail==null && other.getCustomizedemail()==null) || 
             (this.customizedemail!=null &&
              this.customizedemail.equals(other.getCustomizedemail()))) &&
            ((this.customizedfields==null && other.getCustomizedfields()==null) || 
             (this.customizedfields!=null &&
              this.customizedfields.equals(other.getCustomizedfields())));
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
        if (getSwipedata() != null) {
            _hashCode += getSwipedata().hashCode();
        }
        if (getCustomerid() != null) {
            _hashCode += getCustomerid().hashCode();
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
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getCustomizedemail() != null) {
            _hashCode += getCustomizedemail().hashCode();
        }
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DebitReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "DebitReturn"));
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
        elemField.setFieldName("swipedata");
        elemField.setXmlName(new javax.xml.namespace.QName("", "swipedata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerid"));
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
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedemail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedemail"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "customEmail"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedfields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedfields"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields"));
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

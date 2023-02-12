/**
 * ProfileCredit.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class ProfileCredit  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private float amount;

    private java.lang.String last4Digits;

    private java.lang.String userprofileid;

    private java.lang.String merchantpin;

    private java.lang.String ipaddress;

    private java.lang.String currencycode;

    private java.lang.String merchantordernumber;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    public ProfileCredit() {
    }

    public ProfileCredit(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           float amount,
           java.lang.String last4Digits,
           java.lang.String userprofileid,
           java.lang.String merchantpin,
           java.lang.String ipaddress,
           java.lang.String currencycode,
           java.lang.String merchantordernumber,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.amount = amount;
           this.last4Digits = last4Digits;
           this.userprofileid = userprofileid;
           this.merchantpin = merchantpin;
           this.ipaddress = ipaddress;
           this.currencycode = currencycode;
           this.merchantordernumber = merchantordernumber;
           this.customizedemail = customizedemail;
           this.customizedfields = customizedfields;
    }


    /**
     * Gets the acctid value for this ProfileCredit.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this ProfileCredit.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this ProfileCredit.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this ProfileCredit.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this ProfileCredit.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this ProfileCredit.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the amount value for this ProfileCredit.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ProfileCredit.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the last4Digits value for this ProfileCredit.
     * 
     * @return last4Digits
     */
    public java.lang.String getLast4Digits() {
        return last4Digits;
    }


    /**
     * Sets the last4Digits value for this ProfileCredit.
     * 
     * @param last4Digits
     */
    public void setLast4Digits(java.lang.String last4Digits) {
        this.last4Digits = last4Digits;
    }


    /**
     * Gets the userprofileid value for this ProfileCredit.
     * 
     * @return userprofileid
     */
    public java.lang.String getUserprofileid() {
        return userprofileid;
    }


    /**
     * Sets the userprofileid value for this ProfileCredit.
     * 
     * @param userprofileid
     */
    public void setUserprofileid(java.lang.String userprofileid) {
        this.userprofileid = userprofileid;
    }


    /**
     * Gets the merchantpin value for this ProfileCredit.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this ProfileCredit.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the ipaddress value for this ProfileCredit.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this ProfileCredit.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the currencycode value for this ProfileCredit.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this ProfileCredit.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the merchantordernumber value for this ProfileCredit.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this ProfileCredit.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the customizedemail value for this ProfileCredit.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this ProfileCredit.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the customizedfields value for this ProfileCredit.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this ProfileCredit.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileCredit)) return false;
        ProfileCredit other = (ProfileCredit) obj;
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
            ((this.last4Digits==null && other.getLast4Digits()==null) || 
             (this.last4Digits!=null &&
              this.last4Digits.equals(other.getLast4Digits()))) &&
            ((this.userprofileid==null && other.getUserprofileid()==null) || 
             (this.userprofileid!=null &&
              this.userprofileid.equals(other.getUserprofileid()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            ((this.currencycode==null && other.getCurrencycode()==null) || 
             (this.currencycode!=null &&
              this.currencycode.equals(other.getCurrencycode()))) &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
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
        if (getLast4Digits() != null) {
            _hashCode += getLast4Digits().hashCode();
        }
        if (getUserprofileid() != null) {
            _hashCode += getUserprofileid().hashCode();
        }
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getMerchantordernumber() != null) {
            _hashCode += getMerchantordernumber().hashCode();
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
        new org.apache.axis.description.TypeDesc(ProfileCredit.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileCredit"));
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
        elemField.setFieldName("last4Digits");
        elemField.setXmlName(new javax.xml.namespace.QName("", "last4digits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userprofileid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userprofileid"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencycode"));
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
